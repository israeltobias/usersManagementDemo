package es.users.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.users.dto.UserRequest;
import es.users.records.ApiResponse;
import es.users.records.UserResponse;

public class ResponseHandler {

    private static final DateTimeFormatter   FORMATTER      = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Map<String, String> CONSTRAINT_MAP = Map.of("(nif", "nif", "(email", "email");

    private ResponseHandler() {
        super();
    }


    public static ResponseEntity<Object> buildResponse(String message, HttpStatus status, Object data) {
        ApiResponse response = new ApiResponse(message, status.value(), LocalDateTime.now().format(FORMATTER), data);
        return ResponseEntity.status(status).body(response);
    }


    public static ResponseEntity<Object> notAcceptableResponse() {
        return ResponseHandler.buildResponse("Unsupported format: missing or incorrect Accept header",
                HttpStatus.NOT_ACCEPTABLE, new UserResponse("", "", ""));
    }


    public static ResponseEntity<Object> uniqueErrorResponse(DataIntegrityViolationException divex,
            UserRequest userRequest) {
        String constraintName = extractConstraintName(divex);
        String errorMessage = switch (constraintName) {
            case "nif" -> String.format("NIF %s is already registered", userRequest.getNif());
            case "email" -> String.format("Email %s is already in use", userRequest.getEmail());
            default -> "Unique data conflict";
        };
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("field", constraintName.toLowerCase());
        errorDetails.put("value", constraintName.equals("nif") ? userRequest.getNif() : userRequest.getEmail());
        return ResponseHandler.buildResponse(errorMessage, HttpStatus.CONFLICT, errorDetails);
    }


    private static String extractConstraintName(DataIntegrityViolationException divex) {
        String message = divex.getLocalizedMessage().toLowerCase();
        return CONSTRAINT_MAP.entrySet().stream().filter(entry -> message.contains(entry.getKey()))
                .map(Map.Entry::getValue).findFirst().orElse("UNKNOWN_CONSTRAINT");
    }
}
