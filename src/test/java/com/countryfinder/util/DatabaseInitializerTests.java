package com.countryfinder.util;


import com.countryfinder.models.CountryCode;
import com.countryfinder.models.CountryCodeId;
import com.countryfinder.models.DatabaseState;
import com.countryfinder.models.enums.DbStateName;
import com.countryfinder.repositories.CountryCodeDao;
import com.countryfinder.repositories.DatabaseStateDao;
import com.countryfinder.services.DatabaseStateService;
import com.countryfinder.services.SourceDocumentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class DatabaseInitializerTests {

    @Value("${countryFinder.db.expiration.time.days}")
    byte expirationDays;

    @MockBean
    private SourceDocumentService sourceDocumentService;

    @Autowired
    private CountryCodeDao countryCodeDao;

    @Autowired
    private DatabaseStateService stateService;

    @Autowired
    private DatabaseInitializer databaseInitializer;

    @Autowired
    DatabaseStateDao stateDao;

    @BeforeEach
    public void clearDataBase() {
        countryCodeDao.deleteAll();
        stateDao.deleteAll();
    }

    private final List<CountryCode> expectedPhoneCodes = initExpectedPhoneCodes();

    
    @Test
    @DisplayName("DatabaseInitializer: run()")
    public void testRun() throws Exception {
        if (countryCodeDao.count() != 0 || stateDao.count() != 0) {
            throw new RuntimeException("Database should not be initialized");
        }
        when(sourceDocumentService.getAllCountryCodes()).thenReturn(expectedPhoneCodes);
        databaseInitializer.run();
        boolean allWasFoundInDb = expectedPhoneCodes.stream().allMatch(code -> {
            CountryCodeId id = new CountryCodeId(code.getCountry(), code.getCode());
            CountryCode codeFromDB = countryCodeDao.findById(id).orElse(null);
            return code.equals(codeFromDB);
        });
        assertTrue(allWasFoundInDb);
        assertEquals(DataBaseStateFactory.getAvailableState(), stateService.getAvailabilityState());
    }

    @Test
    @DisplayName("DatabaseInitializer: run() empty data case, data not expired")
    public void testRunEmptyData() throws Exception {
        List<CountryCode> emptyList = List.of();

        if (countryCodeDao.count() != 0 || stateDao.count() != 0) {
            throw new RuntimeException("Database should not be initialized");
        }
        countryCodeDao.saveAll(expectedPhoneCodes);

        //expirationDays != 0  and means that today's data will not be expired
        if (expirationDays == 0) {
            throw new RuntimeException("Unable to execute the test. countryFinder.db.expiration.time.days should be -1 or > 0");
        }

        Calendar current = new GregorianCalendar();

        //mark that data was updated today
        stateDao.save(new DatabaseState(DbStateName.LAST_UPDATE_DATE, DateStringConverter.convertToString(current)));
        when(sourceDocumentService.getAllCountryCodes()).thenReturn(emptyList);
        databaseInitializer.run();

        boolean allWasFoundInDb = expectedPhoneCodes.stream().allMatch(code -> {
            CountryCodeId id = new CountryCodeId(code.getCountry(), code.getCode());
            CountryCode codeFromDB = countryCodeDao.findById(id).orElse(null);
            return code.equals(codeFromDB);
        });
        assertTrue(allWasFoundInDb);
        assertEquals(DataBaseStateFactory.getAvailableState(), stateService.getAvailabilityState());
    }


    public List<CountryCode> initExpectedPhoneCodes() {
        List<CountryCode> validCodes = new ArrayList<>();
        
        validCodes.add(new CountryCode(7840, "Sparta"));
        validCodes.add(new CountryCode(7940, "Abkhazia"));
        validCodes.add(new CountryCode(93, "Afghanistan"));
        validCodes.add(new CountryCode(35818, "Åland"));
        validCodes.add(new CountryCode(1684, "American Samoa"));
        validCodes.add(new CountryCode(1264, "Anguilla"));
        validCodes.add(new CountryCode(1268, "Antigua and Barbuda"));
        validCodes.add(new CountryCode(1,"United States"));
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
