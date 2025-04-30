package es.users.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.users.records.ApiResponse;
import es.users.records.UserResponse;

public class ResponseHandler {

    private ResponseHandler() {
        super();
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static ResponseEntity<Object> buildResponse(String message, HttpStatus status, Object data) {
        ApiResponse response = new ApiResponse(message, status.value(), LocalDateTime.now().format(FORMATTER), data);
        return ResponseEntity.status(status).body(response);
    }


    public static ResponseEntity<Object> notAcceptableResponse() {
        return ResponseHandler.buildResponse("Unsupported format: missing or incorrect Accept header",
                HttpStatus.NOT_ACCEPTABLE, new UserResponse("", "", ""));
    }
}
