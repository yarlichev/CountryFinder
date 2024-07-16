package com.countryfinder.services;

import com.countryfinder.models.CountryCode;
import org.jsoup.nodes.Document;

import java.util.List;

public interface DocumentParserService {

    List<CountryCode> getPhoneCodesAndCountries(Document document) throws Exception;
}
