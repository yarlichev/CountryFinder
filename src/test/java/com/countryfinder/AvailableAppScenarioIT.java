package com.countryfinder;

import com.countryfinder.models.CountryCode;
import com.countryfinder.models.dtos.CountryCodesDTO;
import com.countryfinder.repositories.CountryCodeDao;
import com.countryfinder.repositories.DatabaseStateDao;
import com.countryfinder.services.SourceDocumentService;
import com.countryfinder.util.DatabaseInitializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
public class AvailableAppScenarioIT {

    @Autowired
    private MockMvc mockMvc;

    @Value("${spring.datasource.url}")
    private static String url;

    @Value("${countryFinder.db.expiration.time.days}")
    private static String username;

    @Value("${spring.datasource.password}")
    private static String password;

    @Autowired
    private CountryCodeDao countryCodeDao;

    @Autowired
    private DatabaseStateDao databaseStateDao;

    @MockBean
    SourceDocumentService sourceDocumentService;

    @Autowired
    DatabaseInitializer initializer;
    final String JSON = "application/json";

    @Autowired
    private DatabaseInitializer databaseInitializer;

    @Autowired
    private ObjectMapper mapper;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:11.1")
            .withDatabaseName(url).withUsername(username).withPassword(password);

    static {
        postgreSQLContainer.start();
    }

    @Test
    @Order(1)
    @DisplayName("Integration test: send valid request when app is available")
    public void testValidRequest() throws Exception {
        List<CountryCode> extractedPhoneCodes = initExpectedPhoneCodes();

        countryCodeDao.deleteAll();
        databaseStateDao.deleteAll();
        when(sourceDocumentService.getAllCountryCodes()).thenReturn(extractedPhoneCodes);

        //reboot db
        databaseInitializer.run();
        for (CountryCode countryCode : extractedPhoneCodes) {
            int code = countryCode.getCode();
            String NUMBER_TO_SEND = " +" + code + "00000 ";
            String REQUEST = "/countryCode?number=" + NUMBER_TO_SEND;
            List<CountryCode> expected = extractedPhoneCodes.stream()
                    .filter(codeEntity -> codeEntity.getCode() == code).toList();
            CountryCodesDTO expectedDto = new CountryCodesDTO((expected));

            mockMvc.perform(get(REQUEST)).andExpect(status().isOk())
                    .andExpect(content().contentType(JSON))
                    .andExpect(content().json(mapper.writeValueAsString(expectedDto)));
        }
    }

    @Test
    @Order(2)
    @DisplayName("Integration test: send incorrect request when app is available")
    public void testIncorrectRequest() throws Exception {
        String NUMBER_TO_SEND = "A00000 ";
        String REQUEST = "/countryCode?number=" + NUMBER_TO_SEND;

        mockMvc.perform(get(REQUEST)).andExpect(status().is5xxServerError());

    }

    public List<CountryCode> initExpectedPhoneCodes() {
        List<CountryCode> validCodes = new ArrayList<>();

        validCodes.add(new CountryCode(79999, "Sparta"));
        validCodes.add(new CountryCode(7940, "Abkhazia"));
        validCodes.add(new CountryCode(93, "Afghanistan"));
        validCodes.add(new CountryCode(35818, "Åland"));
        validCodes.add(new CountryCode(1684, "American Samoa"));
        validCodes.add(new CountryCode(1264, "Anguilla"));
        validCodes.add(new CountryCode(1268, "Antigua and Barbuda"));
        validCodes.add(new CountryCode(1, "United States"));
        validCodes.add(new CountryCode(1, "Canada"));
        validCodes.add(new CountryCode(5993, "Caribbean Netherlands"));
        validCodes.add(new CountryCode(5994, "Caribbean Netherlands"));
        validCodes.add(new CountryCode(5997, "Caribbean Netherlands"));
        validCodes.add(new CountryCode(6189164, "Christmas Island"));
        validCodes.add(new CountryCode(6189162, "Cocos (Keeling) Islands"));
        validCodes.add(new CountryCode(997, "Kazakhstan"));
        validCodes.add(new CountryCode(76, "Kazakhstan"));
        validCodes.add(new CountryCode(77, "Kazakhstan"));
        validCodes.add(new CountryCode(3906698, "Vatican City State (Holy See)"));
        validCodes.add(new CountryCode(379, "Vatican City State (Holy See)"));
        return validCodes;
    }

}
