package com.mfa272.dialogg;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL;INIT=CREATE SCHEMA IF NOT EXISTS DUMMY;",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=sa",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public class PostTests {
        @Autowired
        private MockMvc mockMvc;

        private MockHttpSession session;
        private String username = "username";
        private String email = "test@email.com";
        private String password = "password";

        @BeforeEach
        void setup() throws Exception {
                mockMvc.perform(post("/register")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", username)
                                .param("email", email)
                                .param("password", password)
                                .with(csrf()));

                MvcResult result = mockMvc.perform(post("/login")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", username)
                                .param("password", password)
                                .with(csrf()))
                                .andReturn();

                session = (MockHttpSession) result.getRequest().getSession(false);

                for (int i = 1; i <= 20; i++) {
                        mockMvc.perform(post("/new")
                                        .session(session)
                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                        .param("content",  String.valueOf(i))
                                        .with(csrf()));
                    }
        }

        @Test
        void newPost() throws Exception {
                mockMvc.perform(post("/new")
                                .session(session)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", username)
                                .param("content", "this is a test post")
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(view().name("redirect:/" + username));
        }

        @Test
        void unauthenticatedPostFails() throws Exception {
                mockMvc.perform(post("/new")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("content", "this is a test post")
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(view().name("redirect:/login"));
        }

        @Test
        void fetchSecondPage() throws Exception{
                MvcResult result = mockMvc.perform(get("/{username}", username)
                                .session(session)
                                .param("page", "1"))
                                .andExpect(status().isOk())
                                .andReturn();

                String content = result.getResponse().getContentAsString();
                for (int i = 10; i > 0; i--) {
                        assert(content.contains("<div>" + String.valueOf(i) + "</div>"));
                }
        }
}
