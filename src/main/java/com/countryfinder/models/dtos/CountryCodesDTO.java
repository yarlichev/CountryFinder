package com.countryfinder.models.dtos;

import com.countryfinder.models.CountryCode;

import java.util.List;

public class CountryCodesDTO {
    private final List<CountryCode> countryCodes;

    public CountryCodesDTO(List<CountryCode> countryCodes) {
        this.countryCodes = countryCodes;
    }

    public List<CountryCode> getCountryCodes() {
        return countryCodes;
    }
}
