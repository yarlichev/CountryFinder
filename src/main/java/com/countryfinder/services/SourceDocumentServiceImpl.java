package com.countryfinder.services;

import com.countryfinder.models.CountryCode;
import com.countryfinder.repositories.SourceDocumentDao;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SourceDocumentServiceImpl implements SourceDocumentService {

    @Value("${countryFinder.source.url}")
    private String sourcePageUrl;
    private DocumentParserService parser;
    private SourceDocumentDao uploader;

    @Autowired
    public SourceDocumentServiceImpl(DocumentParserService parser, SourceDocumentDao uploader) {
        this.parser = parser;
        this.uploader = uploader;
    }

    @Override
    public List<CountryCode> getCountryCodes() throws Exception {
        Document uploadedPage = uploader.uploadDocument(sourcePageUrl);
        List<CountryCode> phonesAndCountries = parser.getPhoneCodesAndCountries(uploadedPage);
        return phonesAndCountries;
    }
}
