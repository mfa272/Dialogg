package com.mfa272.dialogg;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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

class UserAuthenticationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void succesfulRegistration() throws Exception {
		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "testtest")
				.param("email", "test@test.test")
				.param("password", "password")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/login"))
				.andExpect(flash().attribute("successMessage", "You have successfully registered!"));
	}

	@Test
	void usernameAlreadyTaken() throws Exception {

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "testtest")
				.param("email", "test@test.test")
				.param("password", "password")
				.with(csrf()));

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "testtest")
				.param("email", "nottest@nottest.nottest")
				.param("password", "password")
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeHasFieldErrors("user", "username"));
	}

	@Test
	void emailAlreadyTaken() throws Exception {

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "testtest")
				.param("email", "test@test.test")
				.param("password", "password")
				.with(csrf()));

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "nottest")
				.param("email", "test@test.test")
				.param("password", "password")
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeHasFieldErrors("user", "email"));
	}

	@Test
	void successfulLogin() throws Exception {
		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "testtest")
				.param("email", "test@test.test")
				.param("password", "password")
				.with(csrf()));

		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "testtest")
				.param("password", "password")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/posts/new"));
	}

	@Test
	public void logout() throws Exception {

		MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "testtest")
				.param("password", "password")
				.with(csrf()))
				.andReturn()
				.getRequest()
				.getSession(false);

		mockMvc.perform(post("/logout")
				.session(session)
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}
}
