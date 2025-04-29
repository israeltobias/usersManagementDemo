package es.users.services.interfaces;

import java.util.List;
import java.util.Optional;

import es.users.dto.UserRequest;
import es.users.dto.UserResponse;

public interface IUserService {

    public List<UserResponse> getAll();


    public UserResponse createUser(UserRequest user);


    Optional<UserResponse> getUserById(Long userId);


    Optional<UserResponse> updateUserNif(String nif, UserRequest userRequest);


    boolean deleteUserByNif(String nif);
}
