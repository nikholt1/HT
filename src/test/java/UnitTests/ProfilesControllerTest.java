package UnitTests;


import com.example.hometheater.HomeTheaterApplication;
import com.example.hometheater.controller.ProfilesController;
import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.service.ProfileUserService;
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
@WebMvcTest(ProfilesController.class) // test the controller itself, not the test class
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = HomeTheaterApplication.class)

public class ProfilesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileUserService profileUserService;

    @Test
    void profilePageShouldRenderUsers() throws Exception {

        List<ProfileUser> users = List.of(
                new ProfileUser(1, "testUser1", "path/test1"),
                new ProfileUser(2, "testUser2", "path/test2")
        );
        when(profileUserService.getAllUsers()).thenReturn(users);


        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("profiles"))
                .andExpect(model().attribute("users", users));


    }

    @Test
    void profileAddTest() throws Exception {
        mockMvc.perform(get("/profiles/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile_add"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("images"));

    }


    @Test
    void addProfileRedirectedTest() throws Exception {
         ProfileUser user = new ProfileUser();

         mockMvc.perform(post("/profiles/addUser"))
                 .andExpect(status().is3xxRedirection())
                 .andExpect(redirectedUrl("/"));

    }


    @Test
    void selectProfileShouldStoreUserInSessionAndRedirect() throws Exception {
        // Arrange: create test users
        ProfileUser user1 = new ProfileUser(1, "Alice", "path/a.png");
        ProfileUser user2 = new ProfileUser(2, "Bob", "path/b.png");
        List<ProfileUser> users = List.of(user1, user2);


        when(profileUserService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/profiles/select")
                        .param("userId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/videos/browser"))
                .andExpect(request().sessionAttribute("selectedUser", user2));

        verify(profileUserService).getAllUsers();
    }
}
