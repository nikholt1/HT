package com.example.hometheater.controller;


import com.example.hometheater.service.VideoService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoController.class)
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VideoService videoService;

    private Path videoFolder;

    @BeforeAll
    static void setupResources() throws Exception {
        // Ensure src/test/resources/videos exists
        Path testFolder = Path.of("src/test/resources/videos");
        Files.createDirectories(testFolder);
        Path dummyVideo = testFolder.resolve("dummy.mp4");
        if (!Files.exists(dummyVideo)) {
            try (FileOutputStream fos = new FileOutputStream(dummyVideo.toFile())) {
                fos.write(new byte[1024]); // 1 KB dummy content
            }
        }
    }

    @BeforeEach
    void setup() {
        videoFolder = Path.of("src/test/resources/videos");
        when(videoService.getFolderPath()).thenReturn(videoFolder.toString());
        when(videoService.getUserName()).thenReturn("testUser");
    }

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
    @Tag("videoFileDependent")
    void browseVideos_withVideoFile() throws Exception {
        Path videoFile = videoFolder.resolve("test.mp4");
        if (!Files.exists(videoFile)) {
            Files.createFile(videoFile);
        }

        mockMvc.perform(get("/videos/browser"))
                .andExpect(status().isOk())
                .andExpect(view().name("browser"))
                .andExpect(model().attributeExists("previewVideoPath"))
                .andExpect(model().attributeExists("previewVideoName"));
    }

    @Test
    @Tag("videoFileDependent")
    void streamVideo() throws Exception {
        Path filePath = videoFolder.resolve("sample.mp4");
        if (!Files.exists(filePath)) {
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(new byte[1024]);
            }
        }

        mockMvc.perform(get("/videos/stream")
                        .param("filePath", "sample.mp4"))
                .andExpect(status().isOk())
                .andExpect(header().string("Accept-Ranges", "bytes"))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    void setFolder_valid() throws Exception {
        mockMvc.perform(post("/videos/setFolder")
                        .param("folderPath", videoFolder.toString()))
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

    @Test
    void updateFolder_success() throws Exception {
        when(videoService.updateFolderPath(Mockito.anyString())).thenReturn(true);
        mockMvc.perform(post("/videos/updateFolder")
                        .param("folderPath", videoFolder.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videos/settings"));
    }

    @Test
    void updateFolder_failure() throws Exception {
        when(videoService.updateFolderPath(Mockito.anyString())).thenReturn(false);
        mockMvc.perform(post("/videos/updateFolder")
                        .param("folderPath", videoFolder.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videos/browser?error=true"));
    }

    @Test
    void updateUserName_success() throws Exception {
        when(videoService.updateUserName("newName")).thenReturn(true);
        mockMvc.perform(post("/videos/updateUserName")
                        .param("Username", "newName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videos/settings"));
    }

    @Test
    void updateUserName_failure() throws Exception {
        when(videoService.updateUserName("newName")).thenReturn(false);
        mockMvc.perform(post("/videos/updateUserName")
                        .param("Username", "newName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videos/browser?error=true"));
    }

    @Test
    void search_found() throws Exception {
        Path file = videoFolder.resolve("findme.mp4");
        if (!Files.exists(file)) Files.createFile(file);

        mockMvc.perform(get("/videos/search")
                        .param("query", "findme"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("findme.mp4"))
                .andExpect(jsonPath("$[0].type").value("video"));
    }

    @Test
    void search_notFound() throws Exception {
        mockMvc.perform(get("/videos/search")
                        .param("query", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
