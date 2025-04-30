package es.users.records;

public record ApiResponse(String message, int status, String time, Object data) {
}
