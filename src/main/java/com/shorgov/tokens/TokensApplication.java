package com.shorgov.tokens;

import com.shorgov.tokens.model.Role;
import com.shorgov.tokens.model.User;
import com.shorgov.tokens.service.DataService;
import com.shorgov.tokens.service.UserService;
import com.shorgov.tokens.util.TokenManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.HashSet;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TokensApplication {

    public static void main(String[] args) {
        SpringApplication.run(TokensApplication.class, args);
    }

    @Bean
    CommandLineRunner dataRunner(DataService dataService) {
        return arg -> dataService.saveData("Initial data created on startup");
    }

    @Bean
    CommandLineRunner userRunner(UserService userService) {
        return arg -> {
            userService.saveRole(new Role("USER"));
            userService.saveRole(new Role("ADMIN"));


            userService.saveUser(new User("Someone Unknown",
                    "someone@gmail.com",
                    BCrypt.hashpw("qwe", BCrypt.gensalt()),
                    new HashSet<>()));
            userService.saveUser(new User("John Doe",
                    "johndoe@gmail.com",
                    BCrypt.hashpw("asd", BCrypt.gensalt()),
                    new HashSet<>()));

            userService.addRoleToUser("someone@gmail.com", "USER");
            userService.addRoleToUser("someone@gmail.com", "ADMIN");
            userService.addRoleToUser("johndoe@gmail.com", "USER");
        };
    }

}
