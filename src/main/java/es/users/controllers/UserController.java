package es.users.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import es.users.api.UserApiDelegate;
import es.users.context.UserRequestHolder;
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
    private final ResponseHandler    responseHandler;

    public UserController(HttpServletRequest request, UserService userservice, ResponseHandler responseHandler) {
        super();
        this.request = request;
        this.userService = userservice;
        this.responseHandler = responseHandler;
    }


    @Override
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserRequest userRequest) {
        if (Utils.isInvalidAcceptHeader(request)) {
            return responseHandler.notAcceptableResponse();
        }
        UserRequestHolder.set(userRequest);
        UserResponse userResponse = userService.createUser(userRequest);
        return responseHandler.buildResponse("User created successfully!", HttpStatus.OK, userResponse);
    }


    @Override
    public ResponseEntity<Object> getUserByNif(@PathVariable("userNif") String userNif) {
        if (Utils.isInvalidAcceptHeader(request)) {
            return responseHandler.notAcceptableResponse();
        }
        Optional<UserResponse> userResponse = userService.getUserByNif(userNif);
        return userResponse
                .map(usr -> responseHandler.buildResponse("User retrieved successfully.", HttpStatus.OK, usr))
                .orElseGet(() -> responseHandler.buildResponse("User not found!", HttpStatus.NOT_FOUND,
                        new UserResponse("", "", "")));
    }


    @Override
    public ResponseEntity<Object> updateUserByNif(@PathVariable("nif") String nif,
            @Valid @RequestBody UserRequest userRequest) {
        if (Utils.isInvalidAcceptHeader(request)) {
            return responseHandler.notAcceptableResponse();
        }
        UserRequestHolder.set(userRequest);
        Optional<UserResponse> userResponse = userService.updateUserNif(nif, userRequest);
        return userResponse.map(usr -> responseHandler.buildResponse("User updated successfully", HttpStatus.OK, usr))
                .orElseGet(() -> responseHandler.buildResponse("User not found: " + nif, HttpStatus.OK,
                        new UserResponse(nif, "", "")));
    }


    @Override
    public ResponseEntity<Object> deleteUserByNif(@PathVariable("nif") String nif) {
        if (Utils.isInvalidAcceptHeader(request)) {
            return responseHandler.notAcceptableResponse();
        }
        if (userService.deleteUserByNif(nif)) {
            return responseHandler.buildResponse("User deleted sucesfully", HttpStatus.NO_CONTENT,
                    new UserResponse("", "", ""));
        }
        return responseHandler.buildResponse("User not found: " + nif, HttpStatus.BAD_REQUEST, nif);
    }
}
