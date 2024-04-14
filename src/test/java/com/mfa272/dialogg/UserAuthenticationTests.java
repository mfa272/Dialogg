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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
class UserAuthenticationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void succesfulRegistration() throws Exception {
		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "test")
				.param("email", "test@test.test")
				.param("password", "password"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/login"))
				.andExpect(flash().attribute("successMessage", "You have successfully registered!"));
	}

	@Test
	void usernameAlreadyTaken() throws Exception {

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "test")
				.param("email", "test@test.test")
				.param("password", "password"));

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "test")
				.param("email", "nottest@nottest.nottest")
				.param("password", "password"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeHasFieldErrors("user", "username"));
	}

	@Test
	void emailAlreadyTaken() throws Exception {

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "test")
				.param("email", "test@test.test")
				.param("password", "password"));

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "nottest")
				.param("email", "test@test.test")
				.param("password", "password"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeHasFieldErrors("user", "email"));
	}
}
