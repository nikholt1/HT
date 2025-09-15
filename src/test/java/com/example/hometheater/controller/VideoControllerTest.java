package com.example.hometheater.controller;

import com.example.hometheater.service.VideoService;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.web.servlet.MockMvc;

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

    @BeforeEach
    void setup() {
        when(videoService.getFolderPath()).thenReturn("/fake/videos");
        when(videoService.getUserName()).thenReturn("testUser");
    }

    @Test
    void browseVideos() throws Exception {
        // Mock the controller to behave as if there is a video
        mockMvc.perform(get("/videos/browser"))
                .andExpect(status().isOk())
                .andExpect(view().name("browser"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paths"))
                .andExpect(model().attributeExists("previewVideoPath"))
                .andExpect(model().attribute("userName", "testUser"));
    }

    @Test
    void streamVideo() throws Exception {
        // Instead of a real file, just simulate a request; controller will respond with 404 since file doesn't exist
        mockMvc.perform(get("/videos/stream")
                        .param("filePath", "anyfile.mp4"))
                .andExpect(status().isNotFound()); // Safe for CI
    }

    @Test
    void setFolder() throws Exception {
        mockMvc.perform(post("/videos/setFolder")
                        .param("folderPath", "/fake/videos"))
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
                        .param("folderPath", "/fake/videos"))
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

}
