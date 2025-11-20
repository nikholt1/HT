package com.example.hometheater.service.updateService;



import com.example.hometheater.config.AppInfo;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class UpdateChecker {

    private final AppInfo appInfo;

    public UpdateChecker(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public String fetchLatestVersionFromGitHub() {
        String apiUrl = "https://api.github.com/repos/<OWNER>/<REPO>/releases/latest";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON for "tag_name" (e.g., "v1.2.3")
            String json = response.toString();
            String tag = json.split("\"tag_name\":\"")[1].split("\"")[0];
            return tag.startsWith("v") ? tag.substring(1) : tag; // Remove leading 'v' if present
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isVersionNewer(String latest, String current) {
        if (latest == null || current == null) return false;

        String[] latestParts = latest.split("\\.");
        String[] currentParts = current.split("\\.");

        int length = Math.max(latestParts.length, currentParts.length);
        for (int i = 0; i < length; i++) {
            int latestNum = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
            int currentNum = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;

            if (latestNum > currentNum) return true;
            if (latestNum < currentNum) return false;
        }
        return false; // versions are equal
    }
    public void runUpdater() {
        try {
            // Example for JAR updater
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", "Updater.jar");
            pb.inheritIO(); // Optional: show updater console output
            pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void checkAndRunUpdater() {
        String latestVersion = fetchLatestVersionFromGitHub();

        if (isVersionNewer(latestVersion, appInfo.getVersion())) {
            System.out.println("New version detected: " + latestVersion);
            runUpdater();
            System.exit(0); // Exit main app so updater can replace the JAR
        } else {
            System.out.println("App is up to date.");
        }
    }



}
