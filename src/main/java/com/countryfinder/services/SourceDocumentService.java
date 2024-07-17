package com.countryfinder.services;

import com.countryfinder.models.CountryCode;
import java.util.List;

// Service to upload data from external source
public interface SourceDocumentService {
    List<CountryCode> getAllCountryCodes() throws Exception;
}
