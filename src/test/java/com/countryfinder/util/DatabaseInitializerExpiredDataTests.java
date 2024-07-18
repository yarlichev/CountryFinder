package com.countryfinder.util;

import com.countryfinder.models.CountryCode;
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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("expired-data-test")
public class DatabaseInitializerExpiredDataTests {

    @Autowired
    private CountryCodeDao countryCodeDao;

    @Autowired
    private DatabaseStateService stateService;

    @Autowired
    private DatabaseInitializer databaseInitializer;

    @Autowired
    DatabaseStateDao stateDao;

    @Value("${countryFinder.db.expiration.time.days}")
    byte expirationDays;

    @MockBean
    private SourceDocumentService sourceDocumentService;

    @BeforeEach
    public void clearDataBase() {
        countryCodeDao.deleteAll();
        stateDao.deleteAll();
    }

    @Test
    @DisplayName("DatabaseInitializer: run() empty data case, data expired")
    public void testRunEmptyData() throws Exception {
        List<CountryCode> emptyList = List.of();

        if (countryCodeDao.count() != 0 || stateDao.count() != 0) {
            throw new RuntimeException("Database should not be initialized");
        }

        //expirationDays = 0  and means that today's data will be expired
        if (expirationDays != 0) {
            throw new RuntimeException("Unable to execute the test. countryFinder.db.expiration.time.days should be -1 or > 0");
        }

        Calendar current = new GregorianCalendar();

        //mark that data was updated today
        stateDao.save(new DatabaseState(DbStateName.LAST_UPDATE_DATE, DateStringConverter.convertToString(current)));
        when(sourceDocumentService.getAllCountryCodes()).thenReturn(emptyList);
        databaseInitializer.run();

        assertTrue(countryCodeDao.count() == 0);
        assertEquals(DataBaseStateFactory.getNotAvailableState(), stateService.getAvailabilityState());
    }
}
