package com.countryfinder.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "country_code")
@IdClass(CountryCodeId.class)
public class CountryCode {
    @Id
    @Column(name = "code")
    private int code;
    @Id
    @Column(name = "country")
    private String country;

    public CountryCode() {
    }

    public CountryCode(int code, String country) {
        this.code = code;
        this.country = country;
    }

    public int getCode() {
        return code;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CountryCode otherCode)) {
            return false;
        }
        return Objects.equals(this.code, otherCode.code) && Objects.equals(this.country, otherCode.country);
    }
}
