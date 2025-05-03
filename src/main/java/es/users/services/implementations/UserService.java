package es.users.services.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import es.users.dto.UserRequest;
import es.users.entities.User;
import es.users.records.UserResponse;
import es.users.repositories.UserRepository;
import es.users.services.interfaces.IUserService;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService implements IUserService {

    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }


    @Override
    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();
        List<UserResponse> usersResponse = new ArrayList<>();
        users.forEach(item -> {
            UserResponse userResponse = new UserResponse(item.getNif(), item.getName(), item.getEmail());
            usersResponse.add(userResponse);
        });
        return usersResponse;
    }


    @Override
    public UserResponse createUser(UserRequest userRequest) throws DataIntegrityViolationException {
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        user.setNif(userRequest.getNif());
        User userSaved = userRepository.save(user);
        return new UserResponse(userSaved.getNif(), userSaved.getName(), userSaved.getEmail());
    }


    @Override
    public Optional<UserResponse> getUserByNif(String nif) {
        return userRepository.findByNif(nif).map(user -> {
            return new UserResponse(user.getNif(), user.getName(), user.getEmail());
        });
    }


    @Override
    public Optional<UserResponse> updateUserNif(String nif, UserRequest userRequest)
            throws DataIntegrityViolationException {
        return userRepository.findByNif(nif).map(user -> {
            User userToSave = new User();
            userToSave.setId(user.getId());
            userToSave.setNif(userRequest.getNif());
            userToSave.setName(userRequest.getName());
            userToSave.setEmail(userRequest.getEmail());
            User userSaved = userRepository.save(userToSave);
            return new UserResponse(userSaved.getNif(), userSaved.getName(), userSaved.getEmail());
        });
    }


    @Override
    public boolean deleteUserByNif(String nif) {
        int deleteUser = userRepository.deleteByNif(nif);
        return deleteUser != 0;
    }
}
