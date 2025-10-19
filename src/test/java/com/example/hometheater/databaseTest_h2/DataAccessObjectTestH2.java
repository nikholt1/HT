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


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest

public class DataAccessObjectTestH2 {

    @Test
    void contextLoads() {
    }



}
