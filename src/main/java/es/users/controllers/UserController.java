package es.users.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import es.users.Constants;
import es.users.api.UserApiDelegate;
import es.users.dto.UserRequest;
import es.users.dto.UserResponse;
import es.users.services.implementations.UserService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class UserController implements UserApiDelegate {

    private final HttpServletRequest request;
    private final UserService        userService;

    public UserController(HttpServletRequest request, UserService userservice) {
        super();
        this.request = request;
        this.userService = userservice;
    }


    @Override
    public ResponseEntity<UserResponse> saveUser(@Valid @RequestBody UserRequest userRequest) {
        String acceptStr = request.getHeader(Constants.ACCEPT_STRING);
        return switch (acceptStr) {
            case MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE -> {
                UserResponse userResponse = userService.createUser(userRequest);
                yield new ResponseEntity<>(userResponse, HttpStatus.OK);
            }
            default -> new ResponseEntity<>(new UserResponse(), HttpStatus.NOT_ACCEPTABLE);
        };
    }


    @Override
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        String acceptStr = request.getHeader(Constants.ACCEPT_STRING);
        return switch (acceptStr) {
            case MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE -> {
                Optional<UserResponse> userResponse = userService.getUserById(userId);
                yield userResponse.map(usr -> new ResponseEntity<>(usr, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(new UserResponse(), HttpStatus.NOT_FOUND));
            }
            default -> new ResponseEntity<>(new UserResponse(), HttpStatus.NOT_ACCEPTABLE);
        };
    }


    @Override
    public ResponseEntity<UserResponse> updateUserByNif(@PathVariable("nif") String nif,
            @Valid @RequestBody UserRequest userRequest) {
        String acceptStr = request.getHeader(Constants.ACCEPT_STRING);
        return switch (acceptStr) {
            case MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE -> {
                Optional<UserResponse> userResponse = userService.updateUserNif(nif, userRequest);
                yield userResponse.map(usr -> new ResponseEntity<>(usr, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(new UserResponse(), HttpStatus.NOT_FOUND));
            }
            default -> new ResponseEntity<>(new UserResponse(), HttpStatus.NOT_ACCEPTABLE);
        };
    }


    @Override
    public ResponseEntity<UserResponse> deleteUserByNif(@PathVariable("nif") String nif) {
        String acceptStr = request.getHeader(Constants.ACCEPT_STRING);
        return switch (acceptStr) {
            case MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE -> {
                if (userService.deleteUserByNif(nif)) {
                    yield new ResponseEntity<>(new UserResponse(), HttpStatus.NO_CONTENT);
                }
                yield new ResponseEntity<>(new UserResponse(), HttpStatus.BAD_REQUEST);
            }
            default -> new ResponseEntity<>(new UserResponse(), HttpStatus.NOT_ACCEPTABLE);
        };
    }
}
