package es.users.services.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;

import es.users.dto.UserRequest;
import es.users.records.UserResponse;

public interface IUserService {

    public List<UserResponse> getAll();


    public UserResponse createUser(UserRequest user) throws DataIntegrityViolationException;


    Optional<UserResponse> getUserByNif(String userId);


    Optional<UserResponse> updateUserNif(String nif, UserRequest userRequest) throws DataIntegrityViolationException;


    boolean deleteUserByNif(String nif);
}
