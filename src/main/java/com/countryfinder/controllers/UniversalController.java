package com.countryfinder.controllers;

import com.countryfinder.models.CountryCode;
import com.countryfinder.models.DatabaseState;
import com.countryfinder.models.dtos.CountryCodesDTO;
import com.countryfinder.services.CountryCodeService;
import com.countryfinder.services.DatabaseStateService;
import com.countryfinder.util.DataBaseStateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UniversalController {
    private final CountryCodeService countryCodeService;
    private final DatabaseStateService dbStateService;

    @Autowired
    public UniversalController(DatabaseStateService dbStateService, CountryCodeService countryCodeService) {
        this.dbStateService = dbStateService;
        this.countryCodeService = countryCodeService;
    }

    @GetMapping("/status")
    public DatabaseState status() {
        DatabaseState state = dbStateService.getAvailabilltyState();

        if (state == null) {
            return DataBaseStateFactory.getNotAvailableState();
        } else {
            return state;
        }
    }

    @GetMapping("/countryCode")
    public CountryCodesDTO countryCode(@RequestParam(name="number") String countryCode) {
        List<CountryCode> codes = countryCodeService.getSuitableCountryCodes(countryCode);
        return new CountryCodesDTO(codes);
    }

    @ExceptionHandler()
    public ResponseEntity<Object> handleException(
            Exception e) {
        return new ResponseEntity<Object>(
                null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
