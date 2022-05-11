package com.epam.ilyankov.helpdesk.controller;

import com.epam.ilyankov.helpdesk.domain.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user1_mogilev@yopmail.com", password = "P@ssword1")
    void saveComment(){
//        Comment comment = Comment.builder().t.
//        mockMvc.perform(post("/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content())
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isOk());
    }
}