package com.countryfinder.services;

import com.countryfinder.models.CountryCode;
import java.util.List;


public interface SourceDocumentService {
    List<CountryCode> getCountryCodes() throws Exception;
}
