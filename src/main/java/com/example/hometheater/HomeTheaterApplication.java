package com.example.hometheater;

import com.example.hometheater.controller.DesktopApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class HomeTheaterApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
//            DesktopApplicationController.main(new String[]{});
            ConfigurableApplicationContext context = SpringApplication.run(HomeTheaterApplication.class, args);
            SwingUtilities.invokeLater(() -> DesktopApplicationController.main(new String[]{}, context));

        });

        // Start Spring Boot server
//        SpringApplication.run(HomeTheaterApplication.class, args);

    }
}
