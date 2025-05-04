package es.users.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import es.users.context.UserRequestHolder;
import es.users.dto.UserRequest;
import es.users.handler.ResponseHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseHandler responseHandler;

    public GlobalExceptionHandler(ResponseHandler responseHandler) {
        super();
        this.responseHandler = responseHandler;
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handlerDataIntegrityViolationException(DataIntegrityViolationException divex) {
        UserRequest userRequest = UserRequestHolder.get();
        UserRequestHolder.clear();
        return responseHandler.uniqueErrorResponse(divex, userRequest);
    }
}
