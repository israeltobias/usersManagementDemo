package es.users.services.implementations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import es.users.dto.UserRequest;
import es.users.entities.User;
import es.users.records.UserResponse;
import es.users.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService class test")
class UserServiceTest {

    @Mock
    private UserRepository     userRepository;
    @InjectMocks
    private UserService        userService;
    private List<UserResponse> usersResponse;
    private List<User>         users;
    private User               user1;
    private User               user2;
    private UserRequest        userRequest;

    @BeforeEach
    void setUp() {
        user1 = new User("nif1", "name1", "mail1");
        user1.setId(1L);
        user2 = new User("nif2", "name2", "mail2");
        user2.setId(2L);
        users = List.of(user1, user2);
        userRequest = new UserRequest("nifRequest", "nameRequest", "mailRequest");
    }


    @Test
    @DisplayName("getall() should return listuserresponse")
    void getAll_shouldReturnlistUserResponse() {
        when(userRepository.findAll()).thenReturn(users);
        usersResponse = userService.getAll();
        assertNotNull(usersResponse);
        assertEquals(users.size(), usersResponse.size());
        assertThat(usersResponse).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id").isEqualTo(users);
        verify(userRepository).findAll();
    }


    @Test
    @DisplayName("getAll() should return empty list when no users exist")
    void getAll_shouldReturnEmptyListWhennoUsersExist() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        usersResponse = userService.getAll();
        assertThat(usersResponse).isNotNull().isEmpty();
        verify(userRepository).findAll();
    }


    @Test
    @DisplayName("createUser() should return saved user")
    void createUser_shouldReturnSavedUser() {
        when(userRepository.save(any())).thenReturn(user1);
        UserResponse userResponse = userService.createUser(userRequest);
        assertNotNull(userResponse);
        assertEquals(userResponse.nif(), user1.getNif());
        assertEquals(userResponse.name(), user1.getName());
        assertEquals(userResponse.email(), user1.getEmail());
        verify(userRepository).save(any());
    }


    @Test
    @DisplayName("createUser() should return datadataintegrityviolationexception when user not saved")
    void createUser_shouldReturnDataDataIntegrityViolationExceptionWhenUserNotSaved() {
        DataIntegrityViolationException expectedException = new DataIntegrityViolationException("Error saving user");
        when(userRepository.save(any())).thenThrow(expectedException);
        DataIntegrityViolationException thrown = assertThrows(DataIntegrityViolationException.class, () -> {
            userService.createUser(userRequest);
        }, "DataIntegrityViolationException should be thrown if save user fails");
        assertEquals(expectedException.getMessage(), thrown.getMessage());
        verify(userRepository).save(any());
    }


    @Test
    @DisplayName("getUserByNif() should return optional userResponse")
    void getUserByNif_shouldReturnOptionalUserResponse() {
        Optional<User> optionalUser = Optional.of(user1);
        when(userRepository.findByNif(anyString())).thenReturn(optionalUser);
        Optional<UserResponse> optionalResponseUser = userService.getUserByNif(anyString());
        assertThat(optionalResponseUser).isNotEmpty();
        assertEquals(optionalResponseUser.get().nif(), optionalUser.get().getNif());
        assertEquals(optionalResponseUser.get().name(), optionalUser.get().getName());
        assertEquals(optionalResponseUser.get().email(), optionalUser.get().getEmail());
        verify(userRepository).findByNif(anyString());
    }


    @Test
    @DisplayName("getUserByNif() should return empty optional userResponse when user no exists")
    void getUserByNif_shouldReturnEmptyOptionalUserResponse_whenuserNoExists() {
        when(userRepository.findByNif(anyString())).thenReturn(Optional.empty());
        Optional<UserResponse> optionalResponseUser = userService.getUserByNif(anyString());
        assertThat(optionalResponseUser).isEmpty();
        verify(userRepository).findByNif(anyString());
    }


    @Test
    @DisplayName("deleteUserBynif() should return true when user exists")
    void deleteUserBynif_shouldReturnTrue_whenUserExists() {
        when(userRepository.deleteByNif(anyString())).thenReturn(1);
        boolean expectedBool = userService.deleteUserByNif(anyString());
        assertTrue(expectedBool);
        verify(userRepository).deleteByNif(anyString());
    }


    @Test
    @DisplayName("deleteUserBynif() should return true when user no exists")
    void deleteUserBynif_shouldReturnFalse_whenUserNoExists() {
        when(userRepository.deleteByNif(anyString())).thenReturn(0);
        boolean expectedBool = userService.deleteUserByNif(anyString());
        assertFalse(expectedBool);
        verify(userRepository).deleteByNif(anyString());
    }


    @Test
    @DisplayName("updateUser() should return updated user")
    void updateUser_shouldReturnUpdatedUser() {
        when(userRepository.findByNif(anyString())).thenReturn(Optional.of(user1));
        when(userRepository.save(any())).thenReturn(user2);
        Optional<UserResponse> optionalResponseUser = userService.updateUserNif(anyString(), userRequest);
        assertThat(optionalResponseUser).isNotEmpty();
        assertThat(optionalResponseUser.get().nif()).isNotEqualTo(user1.getNif());
        assertThat(optionalResponseUser.get().name()).isNotEqualTo(user1.getName());
        assertThat(optionalResponseUser.get().email()).isNotEqualTo(user1.getEmail());
        assertThat(optionalResponseUser.get().nif()).isEqualTo(user2.getNif());
        assertThat(optionalResponseUser.get().name()).isEqualTo(user2.getName());
        assertThat(optionalResponseUser.get().email()).isEqualTo(user2.getEmail());
        verify(userRepository).findByNif(anyString());
        verify(userRepository).save(any());
    }


    @Test
    @DisplayName("updateUser() should return optional empty when user no exists")
    void updateUser_shouldReturnOptionalEmpty_whenUserNoExists() {
        when(userRepository.findByNif(anyString())).thenReturn(Optional.empty());
        Optional<UserResponse> optionalResponseUser = userService.updateUserNif(anyString(), userRequest);
        assertThat(optionalResponseUser).isEmpty();
        verify(userRepository).findByNif(anyString());
        verify(userRepository, never()).save(any());
    }


    @Test
    @DisplayName("updateUser() should trhow ataIntegrityViolationException when user no updated")
    void updateUser_shouldTrhowDataIntegrityViolationException_whenUserNoUpdated() {
        DataIntegrityViolationException expectedException = new DataIntegrityViolationException("Error updating user");
        when(userRepository.findByNif(anyString())).thenReturn(Optional.of(user1));
        when(userRepository.save(any())).thenThrow(expectedException);
        DataIntegrityViolationException thrown = assertThrows(DataIntegrityViolationException.class, () -> {
            userService.updateUserNif(anyString(), userRequest);
        }, "DataIntegrityViolationException should be thrown if update user fails");
        assertEquals(expectedException.getMessage(), thrown.getMessage());
        verify(userRepository).findByNif(anyString());
        verify(userRepository).save(any());
    }
}
