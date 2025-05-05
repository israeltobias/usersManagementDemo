package es.users.services.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import es.users.dto.UserRequest;
import es.users.entities.User;
import es.users.mappers.UserMapper;
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
        return UserMapper.INSTANCE.listUserToListUserResponse(users);
    }


    @Override
    public UserResponse createUser(UserRequest userRequest) throws DataIntegrityViolationException {
        User user = UserMapper.INSTANCE.userRequestToUser(userRequest);
        User userSaved = userRepository.save(user);
        return new UserResponse(userSaved.getNif(), userSaved.getName(), userSaved.getEmail());
    }


    @Override
    public Optional<UserResponse> getUserByNif(String nif) {
        return userRepository.findByNif(nif).map(UserMapper.INSTANCE::userToUserResponse);
    }


    @Override
    public Optional<UserResponse> updateUserNif(String nif, UserRequest userRequest)
            throws DataIntegrityViolationException {
        return userRepository.findByNif(nif).map(user -> {
            User userToSave = UserMapper.INSTANCE.userRequestToUser(userRequest);
            userToSave.setId(user.getId());
            User usersaved = userRepository.save(userToSave);
            return UserMapper.INSTANCE.userToUserResponse(usersaved);
        });
    }


    @Override
    public boolean deleteUserByNif(String nif) {
        int deleteUser = userRepository.deleteByNif(nif);
        return deleteUser != 0;
    }
}
