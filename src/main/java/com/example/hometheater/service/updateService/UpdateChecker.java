package com.example.hometheater.service.updateService;



import com.example.hometheater.config.AppInfo;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class UpdateChecker {

    private final AppInfo appInfo;

    public UpdateChecker(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public void checkForUpdate() {
        String currentVersion = appInfo.getVersion();
        if (currentVersion == null) {
            System.out.println("[ERROR] Current version is null");
            return;
        }

        // Fetch latest version from GitHub
        String latestVersion = fetchLatestVersionFromGitHub("nikholt1", "HT"); // replace with your GitHub info
        if (latestVersion == null) {
            System.out.println("[ERROR] Could not fetch latest version from GitHub");
            return;
        }

        // Compare versions
        if (isNewerVersion(currentVersion, latestVersion)) {
            System.out.println("[SYSTEM] Newer version available: YOUR VERSION: " + currentVersion + " NEW VERSION: " + latestVersion);
        } else {
            System.out.println("[SYSTEM] System is at the current version: " + currentVersion);
        }
    }

    // Simple numeric version comparison
    private boolean isNewerVersion(String current, String latest) {
        String[] currentParts = current.split("\\.");
        String[] latestParts = latest.split("\\.");
        int length = Math.max(currentParts.length, latestParts.length);

        for (int i = 0; i < length; i++) {
            int currentNum = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            int latestNum = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;

            if (latestNum > currentNum) return true;
            if (latestNum < currentNum) return false;
        }
        return false; // versions are equal
    }

    // Fetch latest release tag from GitHub
    private String fetchLatestVersionFromGitHub(String owner, String repo) {
        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest";

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            // Extract "tag_name", e.g., "v0.0.2"
            String tag = json.toString().split("\"tag_name\":\"")[1].split("\"")[0];
            return tag.startsWith("v") ? tag.substring(1) : tag;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
