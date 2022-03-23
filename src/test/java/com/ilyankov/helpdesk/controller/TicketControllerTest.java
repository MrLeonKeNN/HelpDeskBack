package com.ilyankov.helpdesk.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ilyankov.helpdesk.domain.ticket.State;
import com.ilyankov.helpdesk.domain.ticket.Urgency;
import com.ilyankov.helpdesk.dto.TicketDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = "manager1_mogilev@yopmail.com", password = "P@ssword1", roles = "Employee")
	void saveTicket() throws Exception {
		TicketDto ticketDto = TicketDto.builder().name("Test").description("Test").urgency(Urgency.Average)
				.state(State.Draft.meaning).category("Benefits & Paper Work").build();
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(ticketDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/tickets").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(content().string(String.valueOf(1))).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "manager1_mogilev@yopmail.com", password = "P@ssword1", roles = "Employee")
	void getAllTickets() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/tickets/all/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
}