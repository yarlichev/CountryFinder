package com.countryfinder.util;

import com.countryfinder.models.CountryCode;
import com.countryfinder.models.DatabaseState;
import com.countryfinder.models.enums.DbStateName;
import com.countryfinder.services.DatabaseStateService;
import com.countryfinder.services.CountryCodeService;
import com.countryfinder.services.SourceDocumentService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class DatabaseInitializer  implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final DatabaseState AVAILABLE_STATE = DataBaseStateFactory.getAvailableState();
    private static final DatabaseState NOT_AVAILABLE_STATE = DataBaseStateFactory.getNotAvailableState();

    @Value("${countryFinder.db.expiration.time.days}")
    private byte expirationDays;
    private final CountryCodeService codeService;
    private final DatabaseStateService stateService;
    private final SourceDocumentService sourceDocumentService;


    @Autowired
    public DatabaseInitializer(CountryCodeService codeService, DatabaseStateService stateService, SourceDocumentService documentService) {
        this.codeService = codeService;
        this.stateService = stateService;
        this.sourceDocumentService = documentService;
    }

    @Transactional
    @Override
    public void run(String...args) {
        LOG.info("Application context started. Starting DB initialization...");

        try {
            List<CountryCode> allCodes = sourceDocumentService.getAllCountryCodes();

            if(CollectionUtils.isEmpty(allCodes)) {
                LOG.error("No codes found");
                handleNoDataCase();
            } else {
                addCodesToDataBase(allCodes);
            }
        } catch (Exception e) {
            LOG.error("Failed to init DB", e);
            handleNoDataCase();
        }
    }

    private void addCodesToDataBase(List<CountryCode> allCodes) {
        codeService.clearCountryCodes();
        codeService.addAllCountryCodes(allCodes);
        stateService
                .saveState(new DatabaseState(DbStateName.LAST_UPDATE_DATE,
                        DateStringConverter.convertToString(Calendar.getInstance())));
        stateService.saveState(AVAILABLE_STATE);
    }


    private void handleNoDataCase() {
        DatabaseState lastUpdateState = stateService.findState(DbStateName.LAST_UPDATE_DATE);

        if(lastUpdateState == null || isDataExpired(lastUpdateState.getStateValue())) {
            handleDataExpired();
        } else {
            handleDataNotExpired();
        }
    }

    //if data is expired mark that app is not available
    //and clear expired data
    private void handleDataExpired() {
        stateService.saveState(NOT_AVAILABLE_STATE);
        stateService.removeState(DbStateName.LAST_UPDATE_DATE);
        codeService.clearCountryCodes();
    }

    //if we have non-expired data, lets use it and mark our app as available
    private void handleDataNotExpired() {
        stateService.saveState(AVAILABLE_STATE);
        stateService.saveState(new DatabaseState(DbStateName.LAST_UPDATE_DATE,
                DateStringConverter.convertToString(new GregorianCalendar())));
    }

    private boolean isDataExpired(String date) {

        //-1 means data always valid
        if (expirationDays == -1) {
            return false;
        }

        Calendar lastUpdate = DateStringConverter.convertToDate(date);

        if (lastUpdate == null) {
            return false;
        }
        lastUpdate.roll(Calendar.DAY_OF_MONTH, expirationDays);
        return !lastUpdate.after(Calendar.getInstance());
    }
}
