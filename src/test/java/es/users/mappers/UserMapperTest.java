package es.users.mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.users.dto.UserRequest;
import es.users.entities.User;
import es.users.records.UserResponse;

@DisplayName("UserMapper class test")
class UserMapperTest {

    UserRequest userRequest = new UserRequest("nameRequest", "nifRequest", "emailRequest");
    User        user        = new User("nif", "name", "email");
    User        user2       = new User("nif2", "name2", "email2");

    @Test
    @DisplayName("Should instance be not null")
    void should_InstanceBeNotNull() {
        assertNotNull(UserMapper.INSTANCE, "INSTANCE should not be null");
    }


    @Test
    @DisplayName("Should map userRequest to user")
    void should_mapuserRequestToUser() {
        User userMapper = UserMapper.INSTANCE.userRequestToUser(userRequest);
        assertNotNull(userMapper, "user should not be null");
        assertEquals(userMapper.getName(), userRequest.getName());
        assertEquals(userMapper.getNif(), userRequest.getNif());
        assertEquals(userMapper.getEmail(), userRequest.getEmail());
    }


    @Test
    @DisplayName("userRequestToUser should return null if UserRequest is null")
    void should_userRequestToUserReturnNullIfInputIsNull() {
        User userResponse = UserMapper.INSTANCE.userRequestToUser(null);
        assertNull(userResponse, "userRequestToUser should return null if UserRequest is nul");
    }


    @Test
    @DisplayName("Should map userRequest to user")
    void should_mapuserResponsetToUser() {
        UserResponse userResponse = UserMapper.INSTANCE.userToUserResponse(user);
        assertNotNull(userResponse, "userResponse should not be null");
        assertEquals(userResponse.name(), user.getName());
        assertEquals(userResponse.nif(), user.getNif());
        assertEquals(userResponse.email(), user.getEmail());
    }


    @Test
    @DisplayName("userToUserResponse should return null if UserRequest is null")
    void should_userToUserResponseReturnNullIfInputIsNull() {
        UserResponse userResponse = UserMapper.INSTANCE.userToUserResponse(null);
        assertNull(userResponse, "userToUserResponse should return null if UserRequest is nul");
    }


    @Test
    @DisplayName("Should listUser map to list userResponse")
    void should_listUserMapToListUserResponse() {
        List<User> users = Arrays.asList(user, user2);
        List<UserResponse> userResponses = UserMapper.INSTANCE.listUserToListUserResponse(users);
        assertThat(userResponses).isNotEmpty().hasSameSizeAs(users);
        assertThat(userResponses).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id").isEqualTo(users);
    }


    @Test
    @DisplayName("listUserToListUserResponse should return empty list if listUser is empty")
    void listUserToListUserResponseShouldReturnEmptyListIfInputIsEmpty() {
        // Arrange
        List<User> users = List.of();
        List<UserResponse> responses = UserMapper.INSTANCE.listUserToListUserResponse(users);
        assertNotNull(responses);
        assertTrue(responses.isEmpty(), "list should be empty");
    }


    @Test
    @DisplayName("listUserToListUserResponse should return nul if input is null")
    void listUserToListUserResponseShouldReturnNullIfInputIsNull() {
        List<UserResponse> responses = UserMapper.INSTANCE.listUserToListUserResponse(null);
        assertNull(responses, "listUserToListUserResponse should return nul if input is null");
    }


    @Test
    @DisplayName("listUserToListUserResponse should handle null elements inside User list")
    void listUserToListUserResponseShouldHandleNullElementsInList() {
        List<User> usersWithNull = Arrays.asList(user, null);
        List<UserResponse> responses = UserMapper.INSTANCE.listUserToListUserResponse(usersWithNull);
        assertNotNull(responses);
        assertEquals(2, responses.size(), "Wrong size");
        UserResponse response1 = responses.get(0);
        assertNotNull(response1);
        assertEquals(user.getName(), response1.name());
        assertNull(responses.get(1), "User should be null");
    }
}
