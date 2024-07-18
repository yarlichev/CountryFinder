package com.countryfinder.repositories;

import com.countryfinder.models.CountryCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DataJpaTest
public class CountryCodeDaoTests {

    @Autowired
    private CountryCodeDao countryCodeDao;
    private CountryCode expectedCountryCode;
    private int expectedMaxNumber;


    public  void initCodesList() {
        final CountryCode ARRAKIS = new CountryCode(12, "Arrakis");
        final CountryCode CALADAN = new CountryCode(123, "Caladan");
        final CountryCode GIEDI_PRIME = new CountryCode(11, "Giedi Prime");
        List<CountryCode> countries = new ArrayList<>();

        countries.add(ARRAKIS);
        countries.add(CALADAN);
        countries.add(GIEDI_PRIME);

        expectedCountryCode = countries.stream().map(countryCode -> countryCodeDao.save(countryCode))
                .max(Comparator.comparingInt(CountryCode::getCode)).orElse(null);
        if (expectedCountryCode == null ) {
            throw new RuntimeException("Failed to find expected country code");
        }
        expectedMaxNumber = String.valueOf(expectedCountryCode.getCode()).length();
    }

    @Test
    @DisplayName("CountryCodeDao: searchMaxCodeLength()")
    public void testSearchMaxCodeLength() {
        initCodesList();

        Integer testedMaxNumber = countryCodeDao.searchMaxCodeLength(expectedCountryCode.getCode() + "0000");

        assertEquals(expectedMaxNumber, testedMaxNumber);
    }

    @Test
    @DisplayName("CountryCodeDao: searchCodeByPhoneNumberAndLength()")
    public void searchCodeByPhoneNumberAndLength() {
        initCodesList();

        List<CountryCode> testedList = countryCodeDao.searchCodeByPhoneNumberAndLength(expectedMaxNumber,expectedCountryCode.getCode() + "0000");

        assertIterableEquals(List.of(expectedCountryCode), testedList);
    }





}
