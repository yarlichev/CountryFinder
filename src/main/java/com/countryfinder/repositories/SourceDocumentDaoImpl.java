package com.countryfinder.repositories;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Repository;

import java.io.IOException;


@Repository
public class SourceDocumentDaoImpl implements SourceDocumentDao {

    @Override
    public Document uploadDocument(String sourceUrl) throws IOException {
        return Jsoup.connect(sourceUrl).get();
    }
}
