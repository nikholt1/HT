package UnitTests;


import com.example.hometheater.HomeTheaterApplication;
import com.example.hometheater.config.SecurityConfig;
import com.example.hometheater.controller.ProfilesController;
import com.example.hometheater.controller.VideoController;
import com.example.hometheater.models.MainUser;
import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.service.MainUserService;
import com.example.hometheater.service.ProfileUserService;
import com.example.hometheater.service.VideoService;
import com.example.hometheater.service.updateService.UpdateChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(VideoController.class) // test the controller itself, not the test class
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = HomeTheaterApplication.class)

public class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VideoService videoService;

    @MockitoBean
    private UpdateChecker updateChecker;

    @BeforeEach
    void setup() {
        when(videoService.getFolderPath()).thenReturn("videos");
        when(videoService.getUserName()).thenReturn("Alice");

    }

    @Test
    void browseVideosRedirectsIfNoUserInSession() throws Exception {
        mockMvc.perform(get("/videos/browser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

//    @Test
//    void browseVideosRendersBrowserWithUserInSession() throws Exception {
//        ProfileUser user = new ProfileUser(1, "Alice", "path/a.png");
//        when(videoService.getFolderPath()).thenReturn("videos");
//        when(videoService.getUserName()).thenReturn("Alice");
//
//        mockMvc.perform(get("/videos/browser").sessionAttr("selectedUser", user))
//                .andExpect(status().isOk())
//                .andExpect(view().name("browser"))
//                .andExpect(model().attributeExists("currentUser"))
//                .andExpect(model().attributeExists("items"))
//                .andExpect(model().attributeExists("paths"))
//                .andExpect(model().attributeExists("currentPath"))
//                .andExpect(model().attributeExists("videoFolderPath"))
//                .andExpect(model().attributeExists("userName"));
//    }

    @Test
    void settingsMenuRedirectsIfNoUserInSession() throws Exception {
        mockMvc.perform(get("/videos/settings"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profiles"));
    }

    @Test
    void settingsMenuRendersSettingsWithUser() throws Exception {
        ProfileUser user = new ProfileUser(1, "Alice", "path/a.png");
        when(videoService.getFolderPath()).thenReturn("videos");

        mockMvc.perform(get("/videos/settings").sessionAttr("selectedUser", user))
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attribute("currentUser", user))
                .andExpect(model().attribute("userName", user.getUsername()))
                .andExpect(model().attribute("videoPath", "videos"));
    }

    @Test
    void updateProgressReturnsForbiddenWithoutUser() throws Exception {
        mockMvc.perform(post("/videos/updateProgress")
                        .param("filePath", "test.mp4")
                        .param("seconds", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProgressReturnsOkWithUser() throws Exception {
        ProfileUser user = new ProfileUser(1, "Alice", "path/a.png");

        mockMvc.perform(post("/videos/updateProgress")
                        .sessionAttr("selectedUser", user)
                        .param("filePath", "test.mp4")
                        .param("seconds", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void errorPageRendersCorrectly() throws Exception {
        mockMvc.perform(get("/videos/someError/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorMessage", "someError"));
    }

    @Test
    void updateUserNameRedirectsToSettingsOnSuccess() throws Exception {
        ProfileUser user = new ProfileUser(1, "Alice", "path/a.png");
        when(videoService.updateUserName(user.getUserId(), "Bob")).thenReturn(true);

        mockMvc.perform(post("/videos/updateUserName")
                        .sessionAttr("selectedUser", user)
                        .param("Username", "Bob"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videos/settings"));
    }

    @Test
    void updateUserNameRedirectsToBrowserOnFailure() throws Exception {
        ProfileUser user = new ProfileUser(1, "Alice", "path/a.png");
        when(videoService.updateUserName(user.getUserId(), "Bob")).thenReturn(false);

        mockMvc.perform(post("/videos/updateUserName")
                        .sessionAttr("selectedUser", user)
                        .param("Username", "Bob"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videos/browser?error=true"));
    }
}

