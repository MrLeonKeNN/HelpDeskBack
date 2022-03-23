package com.ilyankov.helpdesk.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = "user1_mogilev@yopmail.com", password = "P@ssword1")
	void getAllCategory() throws Exception {
		String actual =
                "[{\"name\":\"Application & Services\"},{\"name\":\"Benefits & Paper Work\"}," +
                        "{\"name\":\"Hardware & Software\"},{\"name\":\"People Management\"}," +
                        "{\"name\":\"Security & Access\"},{\"name\":\"Workplaces & Facilities\"}]";
		this.mockMvc.perform(get("/categories")).andExpect(status().isOk()).andExpect(content().string(actual));
	}
}