package es.users.services.implementations;

import java.util.ArrayList;
import java.util.List;

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
            userResponse.setNif(item.getNif());
            userResponse.setNombre(item.getName());
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
        userRepository.save(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setNif(userRequest.getNif());
        userResponse.setNombre(userRequest.getName());
        return userResponse;
    }
}
