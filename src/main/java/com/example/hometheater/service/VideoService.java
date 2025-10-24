package com.example.hometheater.service;


import com.example.hometheater.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;


@Service
public class VideoService {

    private final VideoRepository videoRepository;
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


    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }


    public String getFolderPath() {
        return videoRepository.getFolderPath();
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
            System.out.println("[SYSTEM] Folder successfully updated to: " + this.folderPath);
            try {
                videoRepository.updateFolderPath(this.folderPath);
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
        return videoRepository.getUserName();
    }
    public boolean updateUserName(int user_Id, String newUsername) {
        return videoRepository.updateUserName(user_Id, newUsername);

    }


}
