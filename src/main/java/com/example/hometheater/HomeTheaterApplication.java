package com.example.hometheater;

import com.example.hometheater.controller.DesktopApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class HomeTheaterApplication {

    public static void main(String[] args) {

        System.setProperty("java.awt.headless", "false");
        // 1. Start Spring Boot on the main thread
        ConfigurableApplicationContext context =
                SpringApplication.run(HomeTheaterApplication.class, args);

        // 2. Then start Swing on EDT
        SwingUtilities.invokeLater(() -> {
            DesktopApplicationController.main(new String[]{}, context);
        });
    }

        // Start Spring Boot server
//        SpringApplication.run(HomeTheaterApplication.class, args);

}

//    public static void main(String[] args) {
//        SpringApplication.run(HomeTheaterApplication.class, args);
//    }
//
//    @Bean
//    public CommandLineRunner init(DatabaseUtils databaseUtils) {
//        return args -> {
//            MainUser mainUser = databaseUtils.getMainUsers();
//            if (mainUser == null) {
//                databaseUtils.mainUserFirstInitializer();
//                mainUser = databaseUtils.getMainUsers();
//            }
//            System.out.println("MainUser -> username: " + mainUser.getUsername() + ", password: " + mainUser.getPassword());
//        };
//    }



