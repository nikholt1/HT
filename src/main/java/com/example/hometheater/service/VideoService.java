package com.example.hometheater.service;


import com.example.hometheater.repository.VideoRepository;
import org.springframework.stereotype.Service;


@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private String folderPath;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }


    public String getFolderPath() {
        return videoRepository.getFolderPath();
    }

    public boolean updateFolderPath(String newPath) {
        return videoRepository.updateFolderPath(newPath);
    }

    public String getUserName() {
        return videoRepository.getUserName();
    }
    public boolean updateUserName(String newName) {
        return videoRepository.updateUserName(newName);
    }


}
