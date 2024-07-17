package com.countryfinder.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MainPageController.class)
public class MainPageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testMainPageEndpoint() throws Exception {
        final String MAIN_PAGE_ENDPOINT = "/";
        final String HTML = "text/html;charset=UTF-8";
        mockMvc.perform(get(MAIN_PAGE_ENDPOINT)).andExpect(status().isOk())
                .andExpect(content().contentType(HTML));
    }
}
