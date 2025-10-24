package com.example.hometheater;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Profile("!test")
public class DatafolderResourcesInitializer {


    public void start() {
        try {
            Path dataFolder = Paths.get("../data"); // your existing data folder

            // Ensure 'profileImages' folder exists
            Path profileImages = dataFolder.resolve("profileImages");
            if (!Files.exists(profileImages)) {
                Files.createDirectory(profileImages);
                System.out.println("[SYSTEM] Created folder: " + profileImages.toAbsolutePath());
            } else {
                System.out.println("[SYSTEM] Folder already exists: " + profileImages.toAbsolutePath());
            }

            // Ensure 'videos' folder exists
            Path videos = dataFolder.resolve("videos");
            if (!Files.exists(videos)) {
                Files.createDirectory(videos);
                System.out.println("[SYSTEM] Created folder: " + videos.toAbsolutePath());
            } else {
                System.out.println("[SYSTEM] Folder already exists: " + videos.toAbsolutePath());
            }

            // Video categories
            String[] categories = {
                    "Horror", "Sci-Fi", "Documentary", "Home Videos",
                    "Adventure", "Thriller", "Comedy", "Crime",
                    "Action", "Drama", "Reality", "Romance"
            };

            // Create category folders inside 'videos'
            for (String category : categories) {
                Path categoryFolder = videos.resolve(category);
                if (!Files.exists(categoryFolder)) {
                    Files.createDirectory(categoryFolder);
                    System.out.println("[SYSTEM] Created category folder: " + categoryFolder.toAbsolutePath());
                } else {
                    System.out.println("[SYSTEM] Category folder already exists: " + categoryFolder.toAbsolutePath());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

