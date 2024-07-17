package com.countryfinder.controllers;

import com.countryfinder.models.DatabaseState;
import com.countryfinder.services.CountryCodeService;
import com.countryfinder.services.DatabaseStateService;
import com.countryfinder.util.DataBaseStateFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UniversalController.class)
public class UniversalControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryCodeService countryCodeService;

    @MockBean
    private DatabaseStateService dbStateService;

    @Autowired
    ObjectMapper mapper;


    @Test
    public void testStatusEndpoint() throws Exception {
        final String STATUS_ENDPOINT = "/status";
        final String JSON = "application/json";
        DatabaseState available = DataBaseStateFactory.getAvailableState();

        when(dbStateService.getAvailabilityState()).thenReturn(available);
        mockMvc.perform(get(STATUS_ENDPOINT)).andExpect(status().isOk())
                .andExpect(content().contentType(JSON))
                .andExpect(content().string(mapper.writeValueAsString(available)));
    }
}
