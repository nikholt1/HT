package com.example.hometheater;

import com.example.hometheater.controller.DesktopApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
@SpringBootApplication
public class HomeTheaterApplication {

    public static void main(String[] args) {

        // 1. Create SpringApplication instance
        SpringApplication app = new SpringApplication(HomeTheaterApplication.class);

        // 2. Disable headless mode so Swing can show windows
        app.setHeadless(false);

        // 3. Start Spring Boot and get the context
        ConfigurableApplicationContext context = app.run(args);

        // 4. Start Swing GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            DesktopApplicationController.main(new String[]{}, context);
        });
    }
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



