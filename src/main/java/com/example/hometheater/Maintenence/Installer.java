package com.example.hometheater.Maintenence;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.*;

public class Installer extends JFrame {

    private JTextArea logArea;
    private JProgressBar progressBar;
    private Image iconImage;
    private Path installPath; // user-specified installation directory

    public Installer(Path installPath) {
        super("Installer");
        this.installPath = installPath; // use chosen directory

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        // Load icon
        try {
            File iconFile = new File("src/main/resources/static/images/imagesTemplates/R.png");
            if (iconFile.exists()) {
                iconImage = ImageIO.read(iconFile);
                setIconImage(iconImage);
            } else {
                System.err.println("⚠️  Icon file not found at: " + iconFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        add(scrollPane, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        setVisible(true);

        // Run installation task (currently commented)
        new InstallerTask().execute();
    }

    // Pre-installation window to select directory
    private static Path showPreInstallWindow() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Installation Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().toPath();
        } else {
            // User canceled, exit
            System.exit(0);
            return null;
        }
    }

    // Show completion window
    private void showCompletionWindow() {
        JFrame completeFrame = new JFrame("Installer");
        completeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        completeFrame.setSize(400, 500);
        completeFrame.setLocationRelativeTo(null);

        // Apply the same icon
        if (iconImage != null) {
            completeFrame.setIconImage(iconImage);
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setText("Installation complete.\nThe program was installed at:\n" + installPath.toAbsolutePath()
                + "\n\nOn the first login the default username is 'Admin' and password is 'admin' this can be changed after logging in\n\nPress 'Finish' to close the installer.");
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(messageArea, BorderLayout.CENTER);

        JButton finishButton = new JButton("Finish");
        finishButton.addActionListener(e -> System.exit(0));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(finishButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        completeFrame.add(panel);
        completeFrame.setVisible(true);
    }

    private class InstallerTask extends SwingWorker<Void, String> {

        @Override
        protected Void doInBackground() {
            try {
                publish("Starting installation...\n");

                // Create base folder inside user-selected install path
                Path baseDir = installPath.resolve("BaseFolder");
                publish("Creating base folder at: " + baseDir.toAbsolutePath() + "\n");
                Files.createDirectories(baseDir);

                // Define folder structure
                Path dataFolder = baseDir.resolve("datafolder");
                Path profileImages = dataFolder.resolve("profileImages");
                Path videosFolder = dataFolder.resolve("videos");

                String[] categories = {
                        "Action", "Adventure", "Comedy", "Crime", "Documentary",
                        "Drama", "Home-Videos", "Horror", "Reality", "Romance",
                        "Sci-Fi", "Thriller"
                };

                // Create datafolder and subfolders
                publish("Creating folder structure...\n");
                Files.createDirectories(profileImages);
                Files.createDirectories(videosFolder);
                for (String category : categories) {
                    Files.createDirectories(videosFolder.resolve(category));
                }
                setProgress(20);

                // Download assets (currently commented)
                publish("Downloading assets...\n");
//        Path assetDest = profileImages.resolve("icon.png");
//        downloadFile("https://example.com/assets/icon.png", assetDest);
//        setProgress(60);

                // Download main JAR (currently commented)
//        publish("Downloading main JAR...\n");
//        Path jarDest = baseDir.resolve("app.jar");
//        downloadFile("https://example.com/app.jar", jarDest);
//        setProgress(90);

                // Launch application (currently commented)
//        publish("Launching application...\n");
//        ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarDest.toString());
//        pb.directory(baseDir.toFile());
//        pb.start();
//        setProgress(100);

                publish("Installation complete!");
            } catch (Exception e) {
                publish("Error: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void process(java.util.List<String> chunks) {
            for (String msg : chunks) {
                logArea.append(msg);
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }
        }

        private void downloadFile(String urlString, Path destination) throws IOException {
            publish("Downloading from: " + urlString + "\n");
            URL url = new URL(urlString);
            try (InputStream in = url.openStream()) {
                Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        @Override
        protected void done() {
            progressBar.setValue(100);

            // Show completion window after task is done
            SwingUtilities.invokeLater(() -> {
                dispose(); // Close installer window
                showCompletionWindow();
            });
        }
    }
    public static void main(String[] args) {
        // Default installation path
        String defaultPath = System.getProperty("user.home");

        JTextField pathField = new JTextField(defaultPath, 30);

        Object[] message = {
                "Enter installation directory:", pathField
        };

        Object[] options = {"Continue"}; // Single button

        int result = JOptionPane.showOptionDialog(
                null,
                message,
                "Installation Directory",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        // If the user closes the dialog window, exit
        if (result != 0) {
            System.exit(0);
        }

        Path chosenPath = Path.of(pathField.getText());

        // Launch installer using the chosen directory
        SwingUtilities.invokeLater(() -> new Installer(chosenPath));
    }

}
