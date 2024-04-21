package com.mfa272.dialogg;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL;INIT=CREATE SCHEMA IF NOT EXISTS DUMMY;",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})

public class FollowTests {
    @Autowired
    private MockMvc mockMvc;
    private HashMap<String, String> userCredentials = new HashMap<>();

    @BeforeEach
    void setup() throws Exception {
        for (int i = 1; i <= 10; i++) {
            String username = "username" + i;
            String password = "password";
            mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", username)
                    .param("email", username + "@" + "email.e")
                    .param("password", password)
                    .with(csrf()));
            userCredentials.put("username" + i, "password");
        }
    }

    @Test
    void everybodyFollowsUsername2() throws Exception {
        for (int i = 1; i <= 10; i++) {
            mockMvc.perform(post("/follow/username2")
                    .with(user("username" + i))
                    .with(csrf()));
        }
        MvcResult result = mockMvc.perform(get("/{username}/followers", "username2")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("followers"))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        for (int i = 10; i > 0; i--) {
            assert (content.contains("<div>" + "username" + i + "</div>"));
        }
    }

    @Test
    void username1FollowsEverybody() throws Exception {
        for (int i = 2; i <= 10; i++) {
            mockMvc.perform(post("/follow/username" + i)
                    .with(user("username1"))
                    .with(csrf()));
        }

        MvcResult result = mockMvc.perform(get("/username1/followed")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("followed"))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        for (int i = 2; i <= 10; i++) {
            assert (content.contains("<div>username" + i + "</div>"));
        }
    }
}
