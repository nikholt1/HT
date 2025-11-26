package com.example.hometheater.maintenence;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Timer;
import java.util.TimerTask;

public class Updater extends JFrame {

    private JLabel statusLabel;
    private JProgressBar progressBar;

    // Hardcoded GitHub latest release URL
    private static final String RELEASE_URL = "https://api.github.com/repos/myusername/MyApp/releases/latest";
    private static final String FILE_TO_REPLACE = "MyApp.jar"; // file in same folder

    public Updater() {
        super("Updater");
        setSize(400, 120);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        statusLabel = new JLabel("Preparing to update...", SwingConstants.CENTER);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        add(statusLabel, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);

        // Wait 10 seconds then start update
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                fetchAndUpdate();
            }
        }, 10000);
    }

    private void fetchAndUpdate() {
        try {
            statusLabel.setText("Checking for latest version...");

            HttpURLConnection conn = (HttpURLConnection) new URL(RELEASE_URL).openConnection();
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            InputStream in = conn.getInputStream();
            String json = new String(in.readAllBytes());
            in.close();

            // Very simple JSON parsing (no library)
            String downloadUrl = parseDownloadUrl(json, FILE_TO_REPLACE);
            if (downloadUrl == null) {
                statusLabel.setText("No update found.");
                return;
            }

            statusLabel.setText("Downloading latest version...");
            File tempFile = new File(FILE_TO_REPLACE + ".new");
            try (InputStream downloadStream = new URL(downloadUrl).openStream()) {
                Files.copy(downloadStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            statusLabel.setText("Replacing old version...");
            File oldFile = new File(FILE_TO_REPLACE);
            if (oldFile.exists()) {
                oldFile.renameTo(new File(FILE_TO_REPLACE + ".bak"));
            }
            tempFile.renameTo(oldFile);

            statusLabel.setText("Update complete!");
            progressBar.setIndeterminate(false);
            progressBar.setValue(100);

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Update failed: " + e.getMessage());
        }
    }

    // Minimal JSON parsing to get download URL for the file
    private String parseDownloadUrl(String json, String fileName) {
        int fileIndex = json.indexOf("\"name\":\"" + fileName + "\"");
        if (fileIndex == -1) return null;
        int urlIndex = json.lastIndexOf("\"browser_download_url\":\"", fileIndex);
        if (urlIndex == -1) return null;
        int start = urlIndex + 22;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Updater::new);
    }
}
