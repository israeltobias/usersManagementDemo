package es.users.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.users.dto.UserRequest;

@DisplayName("UserRequestHolder class test")
class UserRequestHolderTest {

    UserRequest userRequest1;
    UserRequest userRequest2;

    @BeforeEach
    void setUp() {
        userRequest1 = new UserRequest("nif1", "nombre1", "email1");
        userRequest2 = new UserRequest("nif2", "nombre2", "email2");
        UserRequestHolder.clear();
    }


    @AfterEach
    void clear() {
        UserRequestHolder.clear();
    }


    @Test
    @DisplayName("Should have private constructor")
    void should_HavePrivateConstructor() throws NoSuchMethodException {
        Constructor<UserRequestHolder> constructor = UserRequestHolder.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor should be private");
        constructor.setAccessible(true);
        assertDoesNotThrow(() -> {
            constructor.newInstance();
        }, "Private builder instantiation should not fail");
    }


    @Test
    @DisplayName("Should set and get user request")
    void should_setAndGetUserRequest() {
        UserRequestHolder.set(userRequest1);
        UserRequest retrievedUserRequest = UserRequestHolder.get();
        assertNotNull(retrievedUserRequest);
        assertSame(userRequest1, retrievedUserRequest, "User retrieved should be the same as userRequest");
        assertThat(userRequest1).usingRecursiveComparison().isEqualTo(retrievedUserRequest);
    }


    @Test
    @DisplayName("get() hould return null when not set")
    void should_returNullWhenNotSet() {
        UserRequest retrievedUserRequest = UserRequestHolder.get();
        assertNull(retrievedUserRequest, "get() should return null if not set");
    }


    @Test
    @DisplayName("clear() should remove userRequest holder")
    void should_clearRemoveUserRequest() {
        UserRequestHolder.set(userRequest1);
        assertNotNull(UserRequestHolder.get(), "UserRequest should be setted before clear()");
        UserRequestHolder.clear();
        UserRequest retrievedRequest = UserRequestHolder.get();
        assertNull(retrievedRequest, "UserRequest should be null after clear()");
    }


    @Test
    @DisplayName("")
    void should_OverwritePreviousRequest() {
        UserRequestHolder.set(userRequest1);
        UserRequestHolder.set(userRequest2);
        UserRequest retrievedUserRequest = UserRequestHolder.get();
        assertNotNull(retrievedUserRequest, "Retrieved user should not be null");
        assertNotSame(userRequest1, retrievedUserRequest, "Retrived user should not be same as userRequest1");
        assertSame(retrievedUserRequest, retrievedUserRequest, "Retrived user should be same as userRequest1");
        assertThat(retrievedUserRequest).usingRecursiveComparison()
                .withFailMessage("Retrived user should be not recursive equal to userRequest1")
                .isNotEqualTo(userRequest1);
        assertThat(retrievedUserRequest).usingRecursiveComparison()
                .withFailMessage("Retrived user should be recusive equal to userRequest1").isEqualTo(userRequest2);
    }


    @Test
    @DisplayName("get() after clear() and set() should return new UserRequest")
    void should_getAfterClearAndSetdReturnNewRequest() {
        // Arrange
        UserRequestHolder.set(userRequest1);
        UserRequestHolder.clear();
        // Act
        UserRequestHolder.set(userRequest2);
        UserRequest retrievedUserRequest = UserRequestHolder.get();
        // Assert
        assertNotNull(retrievedUserRequest, "Retrieved user should not be null");
        assertSame(userRequest2, retrievedUserRequest, "Retrieved user should be equal to userRequest2");
        assertThat(retrievedUserRequest).usingRecursiveComparison()
                .withFailMessage("Retrieved user should be recursive equal to userRequest2").isEqualTo(userRequest2);
    }
}
