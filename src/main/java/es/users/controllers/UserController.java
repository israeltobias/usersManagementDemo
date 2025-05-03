package es.users.controllers;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import es.users.api.UserApiDelegate;
import es.users.dto.UserRequest;
import es.users.handler.ResponseHandler;
import es.users.records.UserResponse;
import es.users.services.implementations.UserService;
import es.users.util.Utils;
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
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserRequest userRequest) {
        if (Utils.isInvalidAcceptHeader(request)) {
            return ResponseHandler.notAcceptableResponse();
        }
        try {
            UserResponse userResponse = userService.createUser(userRequest);
            return ResponseHandler.buildResponse("User created successfully!", HttpStatus.OK, userResponse);
        } catch (DataIntegrityViolationException divex) {
            return ResponseHandler.uniqueErrorResponse(divex, userRequest);
        }
    }


    @Override
    public ResponseEntity<Object> getUserByNif(@PathVariable("userNif") String userNif) {
        if (Utils.isInvalidAcceptHeader(request)) {
            return ResponseHandler.notAcceptableResponse();
        }
        Optional<UserResponse> userResponse = userService.getUserByNif(userNif);
        return userResponse
                .map(usr -> ResponseHandler.buildResponse("User retrieved successfully.", HttpStatus.OK, usr))
                .orElseGet(() -> ResponseHandler.buildResponse("User not found!", HttpStatus.NOT_FOUND,
                        new UserResponse("", "", "")));
    }


    @Override
    public ResponseEntity<Object> updateUserByNif(@PathVariable("nif") String nif,
            @Valid @RequestBody UserRequest userRequest) {
        if (Utils.isInvalidAcceptHeader(request)) {
            return ResponseHandler.notAcceptableResponse();
        }
        try {
            Optional<UserResponse> userResponse = userService.updateUserNif(nif, userRequest);
            return userResponse
                    .map(usr -> ResponseHandler.buildResponse("User updated successfully", HttpStatus.OK, usr))
                    .orElseGet(() -> ResponseHandler.buildResponse("User not found: " + nif, HttpStatus.OK,
                            new UserResponse(nif, "", "")));
        } catch (DataIntegrityViolationException divex) {
            return ResponseHandler.uniqueErrorResponse(divex, userRequest);
        }
    }


    @Override
    public ResponseEntity<Object> deleteUserByNif(@PathVariable("nif") String nif) {
        if (Utils.isInvalidAcceptHeader(request)) {
            return ResponseHandler.notAcceptableResponse();
        }
        if (userService.deleteUserByNif(nif)) {
            return ResponseHandler.buildResponse("User deleted sucesfully", HttpStatus.NO_CONTENT,
                    new UserResponse("", "", ""));
        }
        return ResponseHandler.buildResponse("User not found: " + nif, HttpStatus.BAD_REQUEST, nif);
    }
}
