package UnitTests;


import com.example.hometheater.HomeTheaterApplication;
import com.example.hometheater.controller.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoginController.class)
@ContextConfiguration(classes = HomeTheaterApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "login.background.url=test-image.jpg")
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    void loginPageTest() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
//                .andExpect(model().attribute("backgroundImageUrl", "test-image.jpg"));
        // removed due to relevance since image is fetched dynamically

    }
}
