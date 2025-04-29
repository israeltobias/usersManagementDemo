package es.users.services.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import es.users.dto.UserRequest;
import es.users.dto.UserResponse;
import es.users.entities.User;
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
            UserResponse userResponse = new UserResponse();
            userResponse.setId(item.getId());
            userResponse.setNif(item.getNif());
            userResponse.setNombre(item.getName());
            userResponse.setEmail(item.getEmail());
            usersResponse.add(userResponse);
        });
        return usersResponse;
    }


    @Override
    public UserResponse createUser(UserRequest userRequest) {
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        user.setNif(userRequest.getNif());
        User userSaved = userRepository.save(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setId(userSaved.getId());
        userResponse.setNif(userSaved.getNif());
        userResponse.setNombre(userSaved.getName());
        userResponse.setEmail(userSaved.getEmail());
        return userResponse;
    }


    @Override
    public Optional<UserResponse> getUserById(Long userId) {
        return userRepository.findById(userId).map(user -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setNif(user.getNif());
            userResponse.setNombre(user.getName());
            userResponse.setEmail(user.getEmail());
            return userResponse;
        });
    }


    @Override
    public Optional<UserResponse> updateUserNif(String nif, UserRequest userRequest) {
        return userRepository.findByNif(nif).map(user -> {
            UserResponse userResponse = new UserResponse();
            user.setName(userRequest.getName());
            user.setNif(userRequest.getNif());
            user.setEmail(userRequest.getEmail());
            User userSaved = userRepository.save(user);
            userResponse.setId(userSaved.getId());
            userResponse.setNif(userSaved.getNif());
            userResponse.setNombre(userSaved.getName());
            userResponse.setEmail(userSaved.getEmail());
            return userResponse;
        });
    }


    @Override
    public boolean deleteUserByNif(String nif) {
        int deleteUser = userRepository.deleteByNif(nif);
        return deleteUser != 0;
    }
}
