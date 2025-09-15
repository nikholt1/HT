package com.example.hometheater.controller;

import com.example.hometheater.service.VideoService;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(VideoController.class)
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VideoService videoService;

    private Path tempDir;

    @BeforeEach
    void setup() throws Exception {
        tempDir = Files.createTempDirectory("videos");
        when(videoService.getFolderPath()).thenReturn(tempDir.toString());
        when(videoService.getUserName()).thenReturn("testUser");
    }

    @Test
    void browseVideos() throws Exception {
        Path videoFile = Files.createFile(tempDir.resolve("test.mp4"));
        mockMvc.perform(get("/videos/browser"))
                .andExpect(status().isOk())
                .andExpect(view().name("browser"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paths"))
                .andExpect(model().attributeExists("previewVideoPath"));
        Files.deleteIfExists(videoFile);
    }

    @Test
    void streamVideo() throws Exception {
        File file = tempDir.resolve("sample.mp4").toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(new byte[1024]);
        }
        mockMvc.perform(get("/videos/stream")
                        .param("filePath", "sample.mp4"))
                .andExpect(status().isOk())
                .andExpect(header().string("Accept-Ranges", "bytes"));
        file.delete();
    }

    @Test
    void setFolder() throws Exception {
        mockMvc.perform(post("/videos/setFolder")
                        .param("folderPath", tempDir.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Folder updated to")));
    }

    @Test
    void settingsMenu() throws Exception {
        mockMvc.perform(get("/videos/settings"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attribute("userName", "testUser"))
                .andExpect(model().attributeExists("videoPath"));
    }

    @Test
    void updateFolder() throws Exception {
        when(videoService.updateFolderPath(Mockito.anyString())).thenReturn(true);
        mockMvc.perform(post("/videos/updateFolder")
                        .param("folderPath", tempDir.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videos/settings"));
    }

    @Test
    void updateUserName() throws Exception {
        when(videoService.updateUserName("newName")).thenReturn(true);
        mockMvc.perform(post("/videos/updateUserName")
                        .param("Username", "newName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videos/settings"));
    }

    @Test
    void search() throws Exception {
        Path videoFile = Files.createFile(tempDir.resolve("findme.mp4"));
        mockMvc.perform(get("/videos/search")
                        .param("query", "findme"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("findme.mp4"))
                .andExpect(jsonPath("$[0].type").value("video"));
        Files.deleteIfExists(videoFile);
    }
}
