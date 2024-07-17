package com.countryfinder.controllers;

import com.countryfinder.models.CountryCode;
import com.countryfinder.models.DatabaseState;
import com.countryfinder.models.dtos.CountryCodesDTO;
import com.countryfinder.services.CountryCodeService;
import com.countryfinder.services.DatabaseStateService;
import com.countryfinder.util.DataBaseStateFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

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
    private ObjectMapper mapper;

    private static final String PHONE_NUMBER = "123456789";
    private static final String JSON = "application/json";
    private static final String COUNTRY_CODE_ENDPOINT = "/countryCode?number=" + PHONE_NUMBER;
    private static final String STATUS_ENDPOINT = "/status";


    @Test
    @DisplayName("UniversalController: status endpoint (should be available)")
    public void testPositiveCaseForStatusEndpoint() throws Exception {
        DatabaseState available = DataBaseStateFactory.getAvailableState();

        when(dbStateService.getAvailabilityState()).thenReturn(available);
        mockMvc.perform(get(STATUS_ENDPOINT)).andExpect(status().isOk())
                .andExpect(content().contentType(JSON))
                .andExpect(content().string(mapper.writeValueAsString(available)));
    }

    @Test
    @DisplayName("UniversalController: status endpoint (should be not available)")
    public void testNegativeCaseForStatusEndpoint() throws Exception {
        DatabaseState notAvailableState = DataBaseStateFactory.getNotAvailableState();

        when(dbStateService.getAvailabilityState()).thenReturn(notAvailableState);
        mockMvc.perform(get(STATUS_ENDPOINT)).andExpect(status().isOk())
                .andExpect(content().contentType(JSON))
                .andExpect(content().string(mapper.writeValueAsString(notAvailableState)));
    }

    @Test
    @DisplayName("UniversalController: countryCode endpoint(should return codes)")
    public void testCountryCodeEndpointPositiveCase() throws Exception {
        List<CountryCode> codesMock = new ArrayList<>();

        codesMock.add(new CountryCode(1, "Narnia"));
        codesMock.add(new CountryCode(1, "EmeraldCity"));

        CountryCodesDTO expectedDto = new CountryCodesDTO(codesMock);

        when(countryCodeService.getSuitableCountryCodes(PHONE_NUMBER)).thenReturn(codesMock);
        mockMvc.perform(get(COUNTRY_CODE_ENDPOINT)).andExpect(status().isOk())
                .andExpect(content().contentType(JSON))
                .andExpect(content().string(mapper.writeValueAsString(expectedDto)));
    }

    @Test
    @DisplayName("UniversalController: countryCode endpoint(should be empty list when service returned null)")
    public void testCountryCodeEndpointNullCase() throws Exception {
        CountryCodesDTO expectedDto = new CountryCodesDTO(List.of());

        when(countryCodeService.getSuitableCountryCodes(PHONE_NUMBER)).thenReturn(null);
        mockMvc.perform(get(COUNTRY_CODE_ENDPOINT)).andExpect(status().isOk())
                .andExpect(content().contentType(JSON))
                .andExpect(content().string(mapper.writeValueAsString(expectedDto)));
    }
}
