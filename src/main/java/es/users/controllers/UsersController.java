package es.users.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import es.users.Constants;
import es.users.api.UsersApiDelegate;
import es.users.dto.UserResponse;
import es.users.services.implementations.UserService;
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
    public ResponseEntity<List<UserResponse>> getUsers() {
        String acceptStr = request.getHeader(Constants.ACCEPT_STRING);
        return switch (acceptStr) {
            case MediaType.APPLICATION_JSON_VALUE -> {
                List<UserResponse> userResponse = userservice.getAll();
                yield new ResponseEntity<>(userResponse, HttpStatus.OK);
            }
            default -> new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_ACCEPTABLE);
        };
    }
}
