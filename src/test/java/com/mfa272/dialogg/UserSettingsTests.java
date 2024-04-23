package com.mfa272.dialogg;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL;INIT=CREATE SCHEMA IF NOT EXISTS DUMMY;",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=sa",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public class UserSettingsTests {

    @Autowired
    private MockMvc mockMvc;

    private static MockHttpSession session;
    private static String username = "testuser";
    private static String password = "password";

    @BeforeAll
    static void setup(@Autowired MockMvc mockMvc) throws Exception {
        // Register and login user to initialize session
        mockMvc.perform(post("/register")
                .param("username", username)
                .param("email", username + "@example.com")
                .param("password", password)
                .with(csrf()));

        session = (MockHttpSession) mockMvc.perform(post("/login")
                .param("username", username)
                .param("password", password)
                .with(csrf()))
                .andReturn()
                .getRequest()
                .getSession(false);
    }

    @Test
    void changeEmailTest() throws Exception {
        mockMvc.perform(post("/updateEmail")
                .session(session)
                .param("email", "newemail@example.com")
                .with(csrf()))
                .andExpect(redirectedUrl("/settings"));
    }

    @Test
    void changePasswordTest() throws Exception {
        mockMvc.perform(post("/updatePassword")
                .session(session)
                .param("password", "newpassword")
                .with(csrf()))
                .andExpect(redirectedUrl("/settings"));
    }

    @Test
    void deleteAccountTest() throws Exception {
        mockMvc.perform(post("/deleteAccount")
                .session(session)
                .with(csrf()))
                .andExpect(redirectedUrl("/login"));
    }

}
