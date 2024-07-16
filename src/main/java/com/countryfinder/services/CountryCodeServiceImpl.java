package com.countryfinder.services;


import com.countryfinder.models.CountryCode;
import com.countryfinder.repositories.CountryCodeDao;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryCodeServiceImpl implements CountryCodeService {

    private CountryCodeDao codesDao;
    public static final String PHONE_PATTERN = " *\\+?\\d{3,} *";
    Logger LOG = LoggerFactory.getLogger(CountryCodeServiceImpl.class);

    @Autowired
    public CountryCodeServiceImpl(CountryCodeDao codesDao) {
        this.codesDao = codesDao;
    }

    @Override
    public void addAllCountryCodes(List<CountryCode> countryCode) {
        codesDao.saveAll(countryCode);
    }

    @Override
    @Transactional
    public List<CountryCode> getSuitableCountryCodes(String number) {
        if(!number.matches(PHONE_PATTERN)){
            String message = "Invalid format for phone number: " + number;
            LOG.error(message);
            throw new RuntimeException(message);
        }
        //remove + and space from phone number string
        String handledNumber = number.replaceAll("[+ ]", "");
        Integer max = codesDao.searchCodeByPhoneNumber(handledNumber);

        if(max == null){
            return List.of();
        }
        return codesDao.searchByMax(max, number);
    }

    @Override
    public void clearCountryCodes() {
        codesDao.deleteAll();
    }
}
