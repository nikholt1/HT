package com.example.hometheater.service.updateService;

import com.example.hometheater.HomeTheaterApplication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class UpdateService {

    public void downloadAndInstallUpdate(String url) throws IOException, InterruptedException {
        // Download the new JAR (or installer)
        // Replace the current JAR (maybe into a temp dir)
        // Restart the application
        restartApp();
    }

    private void restartApp() throws IOException {
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        File currentJar = new File(HomeTheaterApplication.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath());

        if (!currentJar.getName().endsWith(".jar"))
            return; // not running from JAR

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-jar", currentJar.getPath());
        builder.start();
        System.exit(0);
    }
}
