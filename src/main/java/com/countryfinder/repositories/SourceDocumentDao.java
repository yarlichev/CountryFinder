package com.countryfinder.repositories;


import org.jsoup.nodes.Document;
import java.io.IOException;


public interface SourceDocumentDao {

    Document uploadDocument(String sourceUrl) throws IOException;
}
