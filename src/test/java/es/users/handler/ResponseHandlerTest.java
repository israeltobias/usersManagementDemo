package es.users.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.users.dto.UserRequest;
import es.users.records.ApiResponse;
import es.users.records.UserResponse;
import es.users.util.DatabaseIdentifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResponseHandler class test")
class ResponseHandlerTest {

    @Mock
    private DatabaseIdentifier dbIdentifier;
    @InjectMocks
    private ResponseHandler    responseHandler;
    private UserRequest        userRequest;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setNif("12345678A");
        userRequest.setEmail("test@example.com");
        userRequest.setName("Name");
        ;
    }


    @ParameterizedTest
    @CsvSource({
            "Ok, 200, data",
            "not_found, 404, error",
            "no_content, 204, data",
            "bad_request, 400, error"
    })
    @DisplayName("buildResponse should return correctly formatted response")
    void buildResponse_shouldReturnCorrectlyFormattedResponse(String msg, int status, String data) {
        ResponseEntity<Object> response = responseHandler.buildResponse(msg, HttpStatus.valueOf(status), data);
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals(msg, apiResponse.message());
        assertEquals(data, apiResponse.data());
        assertEquals(Integer.valueOf(status), apiResponse.status());
    }


    @Test
    @DisplayName("notAcceptableResponse should return not acceptable status and message")
    void notAcceptableResponse_shouldReturnNotAcceptableStatusAndMessage() {
        ResponseEntity<Object> responseEntity = responseHandler.notAcceptableResponse();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(responseEntity.getBody()).isInstanceOf(ApiResponse.class);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertThat(apiResponse.message()).isEqualTo("Unsupported format: missing or incorrect Accept header");
        assertThat(apiResponse.status()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(apiResponse.data()).isEqualTo(new UserResponse("", "", ""));
    }


    @Test
    @DisplayName("uniqueErrorResponse when nif constraint violated should return conflict with nif details")
    void uniqueErrorResponse_whenNifConstraintViolated_shouldReturnConflictWithNifDetails() {
        // Simular la excepción
        String rawDbConstraintName = "public.constraint_index_4";
        ConstraintViolationException cve = new ConstraintViolationException(
                "Constraint violation",
                new SQLException("Detail about SQL error", "23505"),
                rawDbConstraintName);
        DataIntegrityViolationException divex = new DataIntegrityViolationException("Data integrity violation", cve);
        // Mockear DatabaseIdentifier
        Map<String, String> constraintMap = new HashMap<>();
        constraintMap.put(rawDbConstraintName.toLowerCase(), "nif");
        when(dbIdentifier.getConstraintMap()).thenReturn(constraintMap);
        ResponseEntity<Object> responseEntity = responseHandler.uniqueErrorResponse(divex, userRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(responseEntity.getBody()).isInstanceOf(ApiResponse.class);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertThat(apiResponse.message())
                .isEqualTo(String.format("NIF %s is already registered", userRequest.getNif()));
        assertThat(apiResponse.status()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(apiResponse.data()).isInstanceOf(Map.class);
        Map<String, Object> errorDetails = (Map<String, Object>) apiResponse.data();
        assertThat(errorDetails).containsEntry("field", "nif").containsEntry("value", userRequest.getNif());
    }


    @Test
    @DisplayName("uniqueErrorResponse when email constraint violated should return conflict withe mail details")
    void uniqueErrorResponse_whenEmailConstraintViolated_shouldReturnConflictWithEmailDetails() {
        String rawDbConstraintName = "unique_email_constraint";
        ConstraintViolationException cve = new ConstraintViolationException("Constraint violation", new SQLException(),
                rawDbConstraintName);
        DataIntegrityViolationException divex = new DataIntegrityViolationException("Data integrity violation", cve);
        Map<String, String> constraintMap = new HashMap<>();
        constraintMap.put(rawDbConstraintName.toLowerCase(), "email");
        when(dbIdentifier.getConstraintMap()).thenReturn(constraintMap);
        ResponseEntity<Object> responseEntity = responseHandler.uniqueErrorResponse(divex, userRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertThat(apiResponse.message())
                .isEqualTo(String.format("Email %s is already in use", userRequest.getEmail()));
        Map<String, Object> errorDetails = (Map<String, Object>) apiResponse.data();
        assertThat(errorDetails).containsEntry("field", "email").containsEntry("value", userRequest.getEmail());
    }


    @Test
    @DisplayName("uniqueErrorResponse when unknown constraint violated should return conflict with generic message")
    void uniqueErrorResponse_whenUnknownConstraintViolated_shouldReturnConflictWithGenericMessage() {
        String rawDbConstraintName = "some_other_unique_constraint";
        ConstraintViolationException cve = new ConstraintViolationException("Constraint violation", new SQLException(),
                rawDbConstraintName);
        DataIntegrityViolationException divex = new DataIntegrityViolationException("Data integrity violation", cve);
        when(dbIdentifier.getConstraintMap()).thenReturn(Collections.emptyMap());
        ResponseEntity<Object> responseEntity = responseHandler.uniqueErrorResponse(divex, userRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertThat(apiResponse.message()).isEqualTo("Unique data conflict");
        Map<String, Object> errorDetails = (Map<String, Object>) apiResponse.data();
        assertThat(errorDetails).containsEntry("field", "unknown_constraint").containsEntry("value",
                userRequest.getEmail());
    }


    @Test
    @DisplayName("uniqueErrorResponse when constraint name is mapped but not nif or email should return conflict with generic message and mapped field")
    void uniqueErrorResponse_whenConstraintNameIsMappedButNotNifOrEmail_shouldReturnConflictWithGenericMessageAndMappedField() {
        String rawDbConstraintName = "uk_username_field";
        String mappedFieldName = "username";
        ConstraintViolationException cve = new ConstraintViolationException("Constraint violation", new SQLException(),
                rawDbConstraintName);
        DataIntegrityViolationException divex = new DataIntegrityViolationException("Data integrity violation", cve);
        Map<String, String> constraintMap = new HashMap<>();
        constraintMap.put(rawDbConstraintName.toLowerCase(), mappedFieldName);
        when(dbIdentifier.getConstraintMap()).thenReturn(constraintMap);
        ResponseEntity<Object> responseEntity = responseHandler.uniqueErrorResponse(divex, userRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertThat(apiResponse.message()).isEqualTo("Unique data conflict");
        Map<String, Object> errorDetails = (Map<String, Object>) apiResponse.data();
        assertThat(errorDetails).containsEntry("field", mappedFieldName).containsEntry("value", userRequest.getEmail());
    }


    @Test
    @DisplayName("uniqueErrorResponse when cause is not constraintviolationexception should throw class cast exception")
    void uniqueErrorResponse_whenCauseIsNotConstraintViolationException_shouldThrowClassCastException() {
        DataIntegrityViolationException divex = new DataIntegrityViolationException(
                "Data integrity violation",
                new SQLException("Some other SQL issue"));
        assertThrows(ClassCastException.class, () -> {
            responseHandler.uniqueErrorResponse(divex, userRequest);
        }, "Expected ClassCastException when cause is not ConstraintViolationException");
    }
}
