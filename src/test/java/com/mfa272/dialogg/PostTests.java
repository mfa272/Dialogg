package com.mfa272.dialogg;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class PostTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createPost() throws Exception{

		mockMvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", "username")
        .param("email", "test@test.username")
        .param("password", "password"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/login"));

        mockMvc.perform(post("/posts/new").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "username").param("content", "this is a test post"))
                .andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/posts"));
    }
}
