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
                                        .param("content", String.valueOf(i))
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
        void fetchSecondPage() throws Exception {
                MvcResult result = mockMvc.perform(get("/{username}", username)
                                .session(session)
                                .param("page", "1"))
                                .andExpect(status().isOk())
                                .andReturn();

                String content = result.getResponse().getContentAsString();
                for (int i = 10; i > 0; i--) {
                        assert (content.contains("<div>" + String.valueOf(i) + "</div>"));
                }
        }

        @Test
        void replyTest() throws Exception {
                mockMvc.perform(post("/reply")
                                .session(session)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("content", "test reply")
                                .param("postId", "1")
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(view().name("redirect:/thread/1"));

                MvcResult threadResult = mockMvc.perform(get("/thread/1")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andReturn();

                String thread = threadResult.getResponse().getContentAsString();
                assert (thread.contains("test reply"));

                mockMvc.perform(post("/reply")
                                .session(session)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("content", "test reply to reply")
                                .param("postId", "1")
                                .param("replyId", "1")
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(view().name("redirect:/thread/1?replyId=1"));

                threadResult = mockMvc.perform(get("/thread/1?replyId=1")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andReturn();

                thread = threadResult.getResponse().getContentAsString();
                assert (thread.contains("test reply to reply"));
        }

        @Test
        public void deletePost() throws Exception {

                MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/login")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", username)
                                .param("password", password)
                                .with(csrf()))
                                .andReturn()
                                .getRequest()
                                .getSession(false);

                String postContent = "deletion test";
                mockMvc.perform(post("/new")
                                .with(user("username"))
                                .session(session)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("content", postContent)
                                .with(csrf()));

                MvcResult result = mockMvc.perform(get("/")
                                .session(session))
                                .andExpect(status().isOk())
                                .andReturn();

                String content = result.getResponse().getContentAsString();
                assert (content.contains(postContent));

                mockMvc.perform(post("/delete/21")
                                .session(session)
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(view().name("redirect:/"));

                result = mockMvc.perform(get("/")
                                .session(session))
                                .andExpect(status().isOk())
                                .andReturn();

                content = result.getResponse().getContentAsString();
                assert (!content.contains(postContent));
        }

        @Test
        public void deleteReply() throws Exception {
                MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/login")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", username)
                                .param("password", password)
                                .with(csrf()))
                                .andReturn()
                                .getRequest()
                                .getSession(false);

                mockMvc.perform(post("/reply")
                                .session(session)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("content", "reply for deletion test")
                                .param("postId", "1")
                                .with(csrf()));

                mockMvc.perform(post("/delete/1")
                                .param("replyId", "1")
                                .session(session)
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(view().name("redirect:/"));

                MvcResult result = mockMvc.perform(get("/thread/1")
                                .session(session)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andReturn();

                String content = result.getResponse().getContentAsString();
                assert (!content.contains("reply for deletion test"));
        }

        @Test
        public void likeTest() throws Exception {
                mockMvc.perform(post("/reply")
                                .session(session)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("content", "reply for like test")
                                .param("postId", "1")
                                .with(csrf()));

                mockMvc.perform(post("/reply")
                                .session(session)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("content", "subreply for like test")
                                .param("postId", "1")
                                .param("replyId", "1")
                                .with(csrf()));

                mockMvc.perform(post("/register")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", "username2")
                                .param("email", "username2@email.com")
                                .param("password", "password2")
                                .with(csrf()));

                mockMvc.perform(post("/register")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", "username3")
                                .param("email", "username3@email.com")
                                .param("password", "password3")
                                .with(csrf()));
                                
                MvcResult loginResult = mockMvc.perform(post("/login")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", "username2")
                                .param("password", "password2")
                                .with(csrf()))
                                .andReturn();

                MockHttpSession session2 = (MockHttpSession) loginResult.getRequest().getSession(false);

                mockMvc.perform(post("/like")
                                .session(session2)
                                .param("postId", "1")
                                .with(csrf()));

                mockMvc.perform(post("/like")
                                .session(session2)
                                .param("replyId", "1")
                                .with(csrf()));

                mockMvc.perform(post("/like")
                                .session(session2)
                                .param("replyId", "2")
                                .with(csrf()));

                loginResult = mockMvc.perform(post("/login")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", "username3")
                                .param("password", "password3")
                                .with(csrf()))
                                .andReturn();

                session2 = (MockHttpSession) loginResult.getRequest().getSession(false);

                mockMvc.perform(post("/like")
                                .session(session2)
                                .param("postId", "1")
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection());

                mockMvc.perform(post("/like")
                                .session(session2)
                                .param("replyId", "1")
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection());

                mockMvc.perform(post("/like")
                                .session(session2)
                                .param("replyId", "2")
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection());
                MvcResult result = mockMvc.perform(get("/thread/1?replyId=1")
                                .session(session2)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andReturn();

                String content = result.getResponse().getContentAsString();

                String findStr = "Likes: 2";
                int lastIndex = 0;
                int count = 0;
                while (lastIndex != -1) {
                        lastIndex = content.indexOf(findStr, lastIndex);
                        if (lastIndex != -1) {
                                count++;
                                lastIndex += findStr.length();
                        }
                }

                assert (count == 3);

                findStr = "Unlike";
                lastIndex = 0;
                count = 0;
                while (lastIndex != -1) {
                        lastIndex = content.indexOf(findStr, lastIndex);
                        if (lastIndex != -1) {
                                count++;
                                lastIndex += findStr.length();
                        }
                }

                assert (count == 3);
        }
}
