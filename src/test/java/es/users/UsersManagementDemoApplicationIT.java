package es.users;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class UsersManagementDemoApplicationIT {

    @Test
    @DisplayName("Context should load")
    void contextLoads(ApplicationContext context) {
        assertNotNull(context);
        assertTrue(context.containsBean("usersManagementDemoApplication"));
    }


    @Test
    @DisplayName("Main should start app when invoked")
    void main_ShouldStartApplication_WhenInvoked() {
        assertDoesNotThrow(() -> UsersManagementDemoApplication.main(new String[] {}));
    }


    @Test
    @DisplayName("Application should have correct configuration")
    void springBootApplicationAnnotation_ShouldHaveCorrectConfiguration() {
        SpringBootApplication annotation = UsersManagementDemoApplication.class
                .getAnnotation(SpringBootApplication.class);
        assertAll(() -> assertNotNull(annotation, "Missing @SpringBootApplication"),
                () -> assertArrayEquals(new String[] { "es.users" }, annotation.scanBasePackages(),
                        "Incorrect package scanning configuration"));
    }
}
