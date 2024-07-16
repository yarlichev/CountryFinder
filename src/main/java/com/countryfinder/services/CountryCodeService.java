package com.countryfinder.services;


import com.countryfinder.models.CountryCode;
import java.util.List;

public interface CountryCodeService {
    void clearCountryCodes();
    void addAllCountryCodes(List<CountryCode> countryCode);
    List<CountryCode> getSuitableCountryCodes(String number);
}
