package es.users.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import es.users.dto.UserRequest;
import es.users.records.ApiResponse;
import es.users.records.UserResponse;
import es.users.util.DatabaseIdentifier;

@Component
public class ResponseHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DatabaseIdentifier       dbIdentifier;

    public ResponseHandler(DatabaseIdentifier dbIdentifier) {
        this.dbIdentifier = dbIdentifier;
    }


    public ResponseEntity<Object> buildResponse(String message, HttpStatus status, Object data) {
        ApiResponse response = new ApiResponse(message, status.value(), LocalDateTime.now().format(FORMATTER), data);
        return ResponseEntity.status(status).body(response);
    }


    public ResponseEntity<Object> notAcceptableResponse() {
        return buildResponse("Unsupported format: missing or incorrect Accept header", HttpStatus.NOT_ACCEPTABLE,
                new UserResponse("", "", ""));
    }


    public ResponseEntity<Object> uniqueErrorResponse(DataIntegrityViolationException divex, UserRequest userRequest) {
        String constraintName = extractConstraintName(divex);
        String errorMessage = switch (constraintName) {
            case "nif" -> String.format("NIF %s is already registered", userRequest.getNif());
            case "email" -> String.format("Email %s is already in use", userRequest.getEmail());
            default -> "Unique data conflict";
        };
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("field", constraintName.toLowerCase());
        errorDetails.put("value", constraintName.equals("nif") ? userRequest.getNif() : userRequest.getEmail());
        return buildResponse(errorMessage, HttpStatus.CONFLICT, errorDetails);
    }


    private String extractConstraintName(DataIntegrityViolationException divex) {
        ConstraintViolationException constraintViolationException = (ConstraintViolationException) divex.getCause();
        String constrainName = constraintViolationException.getConstraintName().toLowerCase();
        return dbIdentifier.getConstraintMap().entrySet().stream().filter(entry -> constrainName.equals(entry.getKey()))
                .map(Map.Entry::getValue).findFirst().orElse("UNKNOWN_CONSTRAINT");
    }
}
