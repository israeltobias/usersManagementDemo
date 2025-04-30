package es.users.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import es.users.api.UsersApiDelegate;
import es.users.handler.ResponseHandler;
import es.users.records.UserResponse;
import es.users.services.implementations.UserService;
import es.users.util.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class UsersController implements UsersApiDelegate {

    private final HttpServletRequest request;
    private final UserService        userservice;

    public UsersController(HttpServletRequest request, UserService userservice) {
        super();
        this.request = request;
        this.userservice = userservice;
    }


    @Override
    public ResponseEntity<Object> getUsers() {
        if (Utils.isInvalidAcceptHeader(request)) {
            return ResponseHandler.notAcceptableResponse();
        }
        List<UserResponse> userResponse = userservice.getAll();
        return ResponseHandler.buildResponse("Usuarios existentes", HttpStatus.OK, userResponse);
    }
}
