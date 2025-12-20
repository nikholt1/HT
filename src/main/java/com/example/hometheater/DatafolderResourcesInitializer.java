package com.example.hometheater;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Profile("!test")
public class DatafolderResourcesInitializer {

    @PostConstruct
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
                    "Horror", "Sci-Fi", "Documentary", "Home-Videos",
                    "Adventure", "Thriller", "Comedy", "Crime",
                    "Action", "Drama", "Reality", "Romance"
            };

            // Create category folders and check for image
            for (String category : categories) {
                Path categoryFolder = videos.resolve(category);
                if (!Files.exists(categoryFolder)) {
                    Files.createDirectory(categoryFolder);
                    System.out.println("[SYSTEM] Created category folder: " + categoryFolder.toAbsolutePath());
                } else {
                    System.out.println("[SYSTEM] Category folder already exists: " + categoryFolder.toAbsolutePath());
                }

                // Check if image with same name exists in the category folder
                Path jpgImage = categoryFolder.resolve(category.toLowerCase() + ".jpg");
                Path pngImage = categoryFolder.resolve(category.toLowerCase() + ".png");

                if (Files.exists(jpgImage)) {
                    System.out.println("[SYSTEM] Image exists: " + jpgImage.toAbsolutePath());
                } else if (Files.exists(pngImage)) {
                    System.out.println("[SYSTEM] Image exists: " + pngImage.toAbsolutePath());
                } else {
                    System.out.println("[SYSTEM] No image found for category: " + category);

                    // Try to copy a template image from resources/static/images/imagesTemplates
                    String templateFileName = category.toLowerCase() + ".png"; // you can also try .png
                    Resource templateResource = new ClassPathResource("static/images/imagesTemplates/" + templateFileName);

                    if (templateResource.exists()) {
                        try (InputStream in = templateResource.getInputStream()) {
                            Path targetPath = categoryFolder.resolve(templateFileName);
                            Files.copy(in, targetPath);
                            System.out.println("[SYSTEM] Copied template image to: " + targetPath.toAbsolutePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("[SYSTEM] Template image not found in resources for category: " + category);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}