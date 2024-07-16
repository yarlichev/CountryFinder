package com.countryfinder.models;

import java.util.Objects;

// class to support composite primary key
// for CountryCode
public class CountryCodeId {
    private int code;
    private String country;

    public CountryCodeId(){}

    public CountryCodeId(String country, int code){
        this.code = code;
        this.country = country;
    }

    @Override
    public String toString() {
        return code + " " + country;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CountryCodeId)) {
            return false;
        }
        CountryCodeId other = (CountryCodeId) obj;
        return Objects.equals(this.code, other.code) && Objects.equals(this.country, other.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, country);
    }
}
