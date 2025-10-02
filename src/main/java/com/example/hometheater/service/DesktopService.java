package com.example.hometheater.service;

import com.example.hometheater.repository.DesktopRepository;
import com.example.hometheater.repository.VideoRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.nio.file.*;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;

public class DesktopService {

    private final DesktopRepository desktopRepository;
    private String folderPath;

    // Only truly critical system folders are forbidden
    private static final List<Path> FORBIDDEN_PATHS = List.of(
            // Linux / macOS critical dirs
            Paths.get("/etc"),
            Paths.get("/bin"),
            Paths.get("/sbin"),
            Paths.get("/usr"),
            Paths.get("/proc"),
            Paths.get("/sys"),
            Paths.get("/dev"),
            // Windows critical dirs
            Paths.get("C:\\Windows"),
            Paths.get("C:\\Program Files"),
            Paths.get("C:\\Program Files (x86)"),
            Paths.get("C:\\ProgramData")
    );

    // Constructor
    public DesktopService(DesktopRepository desktopRepository) {
        this.desktopRepository = desktopRepository;
    }

    public String getFolderPath() {
        return desktopRepository.getFolderPath();
    }

    public boolean updateFolderPath(String folderPath) {
        try {
            Path path = Paths.get(folderPath).toAbsolutePath().normalize();

            if (!Files.exists(path) || !Files.isDirectory(path)) {
                return false;
            }

            for (Path forbidden : FORBIDDEN_PATHS) {
                Path forbiddenNormalized = forbidden.toAbsolutePath().normalize();
                if (path.startsWith(forbiddenNormalized)) {
                    System.out.println("Attempt to set forbidden folder: " + path);
                    return false;
                }
            }

            this.folderPath = path.toString();
            System.out.println("Folder successfully updated to: " + this.folderPath);
            try {
                desktopRepository.updateFolderPath(this.folderPath);
                return true;
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("Failed to update folder: " + this.folderPath);
            }

        } catch (Exception e) {
            System.err.println("Error updating folder path: " + e.getMessage());
            return false;
        }
        return false;
    }

    public String getUserName() {
        return desktopRepository.getUserName();
    }

    public boolean updateUserName(String newName) {
        return desktopRepository.updateUserName(newName);
    }
    public String getIpv4Address() {
        return desktopRepository.getIPv4Address();
    }
}
