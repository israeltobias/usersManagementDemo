package es.users.exceptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.users.context.UserRequestHolder;
import es.users.dto.UserRequest;
import es.users.handler.ResponseHandler;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ResponseHandler                 mockResponseHandler;
    @InjectMocks
    private GlobalExceptionHandler          globalExceptionHandler;
    private MockedStatic<UserRequestHolder> mockedUserRequestHolder;
    private UserRequest                     mockUserRequest;
    private DataIntegrityViolationException mockDivex;

    @BeforeEach
    void setUp() {
        mockUserRequest = new UserRequest();
        mockUserRequest.setNif("12345678A");
        mockUserRequest.setEmail("test@example.com");
        mockUserRequest.setName("Jperez");
        mockDivex = new DataIntegrityViolationException("Simulated data integrity violation");
        mockedUserRequestHolder = Mockito.mockStatic(UserRequestHolder.class);
    }


    @AfterEach
    void tearDown() {
        if (mockedUserRequestHolder != null) {
            mockedUserRequestHolder.close();
        }
    }


    @Test
    void handlerDataIntegrityViolationException_shouldCallResponseHandlerAndClearHolder() {
        mockedUserRequestHolder.when(UserRequestHolder::get).thenReturn(mockUserRequest);
        ResponseEntity<Object> expectedResponseEntity = ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Error response");
        when(mockResponseHandler.uniqueErrorResponse(mockDivex, mockUserRequest))
                .thenReturn(expectedResponseEntity);
        ResponseEntity<Object> actualResponseEntity = globalExceptionHandler
                .handlerDataIntegrityViolationException(mockDivex);
        assertThat(actualResponseEntity).isEqualTo(expectedResponseEntity);
        assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        mockedUserRequestHolder.verify(UserRequestHolder::get);
        verify(mockResponseHandler).uniqueErrorResponse(mockDivex, mockUserRequest);
        mockedUserRequestHolder.verify(UserRequestHolder::clear);
    }


    @Test
    void handlerDataIntegrityViolationException_whenUserRequestHolderReturnsNull_shouldPassNullToResponseHandler() {
        mockedUserRequestHolder.when(UserRequestHolder::get).thenReturn(null);
        ResponseEntity<Object> expectedResponseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error with null request");
        when(mockResponseHandler.uniqueErrorResponse(mockDivex, null))
                .thenReturn(expectedResponseEntity);
        // Act
        ResponseEntity<Object> actualResponseEntity = globalExceptionHandler
                .handlerDataIntegrityViolationException(mockDivex);
        // Assert
        assertThat(actualResponseEntity).isEqualTo(expectedResponseEntity);
        mockedUserRequestHolder.verify(UserRequestHolder::get);
        verify(mockResponseHandler).uniqueErrorResponse(mockDivex, null);
        mockedUserRequestHolder.verify(UserRequestHolder::clear);
    }
}
