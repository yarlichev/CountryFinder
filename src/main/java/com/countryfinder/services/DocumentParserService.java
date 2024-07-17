package com.countryfinder.services;

import com.countryfinder.models.CountryCode;
import org.jsoup.nodes.Document;

import java.util.List;

//Service for html document parsing
public interface DocumentParserService {

    List<CountryCode> parsePhoneCodesAndCountries(Document document) throws Exception;
}
