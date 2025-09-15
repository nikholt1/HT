package com.example.hometheater.controller;


import com.example.hometheater.service.VideoService;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoController.class)
class VideoControllerTest {

    @Configuration
    static class TestConfig {
        @Bean
        public VideoService videoService() throws Exception {
            VideoService mockService = mock(VideoService.class);
            // Always return a temp folder for CI
            Path tempFolder = Files.createTempDirectory("videos-ci");
            when(mockService.getFolderPath()).thenReturn(tempFolder.toString());
            when(mockService.getUserName()).thenReturn("testUser");
            return mockService;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VideoService videoService;

    @Test
    void browseVideos_emptyFolder() throws Exception {
        mockMvc.perform(get("/videos/browser"))
                .andExpect(status().isOk())
                .andExpect(view().name("browser"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paths"))
                .andExpect(model().attributeExists("previewVideoPath"))
                .andExpect(model().attribute("userName", "testUser"));
    }

    @Test
    void setFolder_valid() throws Exception {
        mockMvc.perform(post("/videos/setFolder")
                        .param("folderPath", videoService.getFolderPath()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Folder updated to")));
    }

    @Test
    void setFolder_invalid() throws Exception {
        mockMvc.perform(post("/videos/setFolder")
                        .param("folderPath", "/invalid/path"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Folder does not exist")));
    }

    @Test
    void settingsMenu() throws Exception {
        mockMvc.perform(get("/videos/settings"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attribute("userName", "testUser"))
                .andExpect(model().attributeExists("videoPath"));
    }

    // You can add more tests for updateFolder, updateUserName, search etc.
    // Avoid any tests that depend on actual video files
}