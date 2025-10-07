package com.example.hometheater;

import com.example.hometheater.controller.DesktopApplicationController;
import com.example.hometheater.models.MainUser;
import com.example.hometheater.service.MainUserService;
import com.example.hometheater.utils.DatabaseUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.swing.*;
import java.sql.SQLException;

@SpringBootApplication
public class HomeTheaterApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeTheaterApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(DatabaseUtils databaseUtils) {
        return args -> {
            MainUser mainUser = databaseUtils.getMainUsers();
            if (mainUser == null) {
                databaseUtils.mainUserFirstInitializer();
                mainUser = databaseUtils.getMainUsers();
            }
            System.out.println("MainUser -> username: " + mainUser.getUsername() + ", password: " + mainUser.getPassword());
        };
    }
}
