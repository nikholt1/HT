package com.example.hometheater.Maintenence;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class Updater extends JFrame {

    private JTextArea logArea;
    private JProgressBar progressBar;

    private final String targetFileName = "app.jar";
    private final String downloadUrl = "https://example.com/app.jar";

    public Updater() {
        super("Updater");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 200);
        setLocationRelativeTo(null);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        add(scrollPane, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        setVisible(true);

        new UpdateTask().execute();
    }

    private class UpdateTask extends SwingWorker<Void, String> {

        @Override
        protected Void doInBackground() {
            try {
                Path currentDir = Paths.get("").toAbsolutePath();
                publish("Current directory: " + currentDir);

                Path targetFile = currentDir.resolve(targetFileName);
                if (!Files.exists(targetFile)) {
                    publish("Target file does not exist: " + targetFileName);
                    return null;
                }

                publish("Updating file: " + targetFileName);

                Path tempFile = currentDir.resolve(targetFileName + ".tmp");

                URL url = new URL(downloadUrl);
                URLConnection conn = url.openConnection();
                int fileSize = conn.getContentLength();
                if (fileSize <= 0) {
                    publish("Warning: Unknown file size, progress may not be accurate.");
                }

                try (InputStream in = conn.getInputStream();
                     OutputStream out = Files.newOutputStream(tempFile)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    int totalRead = 0;

                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;

                        if (fileSize > 0) {
                            int percent = (int) (totalRead * 100L / fileSize);
                            setProgress(percent); // update progress bar like installer
                        }
                    }
                }

                // Replace the old file
                Files.move(tempFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                publish("Update complete!");
                setProgress(100);

            } catch (Exception e) {
                publish("Error: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void process(java.util.List<String> chunks) {
            for (String msg : chunks) {
                logArea.append(msg + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }
        }

        @Override
        protected void done() {
            progressBar.setValue(getProgress()); // ensure progress bar shows 100%
            JOptionPane.showMessageDialog(Updater.this, "Update finished!", "Updater", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Updater::new);
    }
}
