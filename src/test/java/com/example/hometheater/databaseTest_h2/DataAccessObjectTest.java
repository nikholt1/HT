package com.example.hometheater.databaseTest_h2;


// H2 test is a lightweight in-memory database provided as a Maven dependency, that allows us to create a
// temporary database instance, similar to a mock, for integration testing repositories or other components that rely on a database
// this way, we can test the functionality of core components without affecting the production / working database for the program.
//
//
// the application-test.properties file that configures our test environment, containing parameters for the H2 database.
// This way we can control the H2 database and ensure that it stays available and in a consistent state throughout hte entire test lifecycle.
// in the CI pipeline in the workflow on Github, will automatically run all unit test and integration tests given the specific triggers are activated.
// that way, if the trigger for example is on a push or pull request, we can ensure that all integration tests are run without errors and are not failing.


// The @ActiveProfiles dictates to use the application-test.properties profile file.
// This test class builds the h2init.sql and since H2 is not tied to any specific SQL dialect, it allows us to mock the production database, the BEFORE_TEST_METHOD enum
// in the annotation @sql with the classpath to the h2init.sql on the value of the executionPhase builds a fresh database on each test.


// nikholt1 06/10/2025


import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.utils.DataAccessObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(
        scripts = "classpath:h2init.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)public class DataAccessObjectTest {

    @Autowired
    private DataAccessObject repo;
    @Autowired
    DataSource dataSource;

    @BeforeEach
    void printDbUrl() throws SQLException {
        System.out.println("H2 DB URL: " + dataSource.getConnection().getMetaData().getURL());
    }

    // Create
    @Test
    public void addUserTest() throws SQLException {
        repo.addUser(new ProfileUser(1, "testUserName", "testPath/to/test"));
        var list =  repo.getUsers();
        for (ProfileUser u : list) {
            if (u.getUsername().equals("testUserName")) {
                assertThat(u).isNotNull();
                // the images/profileImages gets added in the addUser method
                assertThat(u.getProfilePicturePath()).isEqualTo("testPath/to/test");
            }
        }
    }

    //Read
    @Test
    public void getAllUsersTest() throws SQLException {
        List<ProfileUser> profileUsers = repo.getUsers();
        assertThat(profileUsers).isNotNull();
        assertThat(profileUsers.size()).isEqualTo(2);
        assertThat(profileUsers.get(0).getUsername()).isEqualTo("testUser1");
        assertThat(profileUsers.get(1).getUsername()).isEqualTo("testUser2");
    }


    @Test
    public void getMainUsersTest() throws SQLException {
        var mu = repo.getMainUsers();
        assertThat(mu).isNotNull();
        assertThat(mu.getUsername()).isEqualTo("testMain_User1");
    }

    @Test
    public void deleteUserTest() throws SQLException {
        ProfileUser user = new ProfileUser();
        user.setUsername("tempUser");
        user.setProfilePicturePath("tempPic.jpg");
        repo.addUser(user);


        repo.deleteUser("tempUser");

        var users = repo.getUsers();
        boolean userExists = users.stream()
                .anyMatch(u -> u.getUsername().equals("tempUser"));

        assertThat(userExists).isFalse();
    }
}
