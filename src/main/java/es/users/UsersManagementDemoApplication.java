package es.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "es.users")
public class UsersManagementDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersManagementDemoApplication.class, args);
    }
}
