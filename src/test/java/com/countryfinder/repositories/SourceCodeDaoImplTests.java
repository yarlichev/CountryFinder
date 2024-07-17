package com.countryfinder.repositories;

import com.countryfinder.models.CountryCode;
import com.countryfinder.services.DocumentParserService;
import com.countryfinder.services.DocumentParserServiceImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = SourceCodeDaoImplTests.Config.class)
@TestPropertySource(locations="classpath:application-test.properties")
public class SourceCodeDaoImplTests {
    private final String PROPER_PAGE = "src/test/resources/testPage.html";

    @Value("${countryFinder.source.url}")
    private String sourceUrl;
    private static List<CountryCode> expectedCodes;

    @BeforeAll
    public static void initExpectedPhoneCodesMap() {
        expectedCodes = new ArrayList<>();
        expectedCodes.add(new CountryCode(7840, "Abkhazia"));
        expectedCodes.add(new CountryCode(7940, "Abkhazia"));
        expectedCodes.add(new CountryCode(93, "Afghanistan"));
        expectedCodes.add(new CountryCode(35818, "Åland"));
        expectedCodes.add(new CountryCode(1684, "American Samoa"));
        expectedCodes.add(new CountryCode(1264, "Anguilla"));
        expectedCodes.add(new CountryCode(1268, "Antigua and Barbuda"));
        expectedCodes.add(new CountryCode(1,"United States"));
        expectedCodes.add(new CountryCode(1, "Canada"));
        expectedCodes.add(new CountryCode(5993, "Caribbean Netherlands"));
        expectedCodes.add(new CountryCode(5994, "Caribbean Netherlands"));
        expectedCodes.add(new CountryCode(5997, "Caribbean Netherlands"));
        expectedCodes.add(new CountryCode(6189164, "Christmas Island"));
        expectedCodes.add(new CountryCode(6189162, "Cocos (Keeling) Islands"));
        expectedCodes.add(new CountryCode(997, "Kazakhstan"));
        expectedCodes.add(new CountryCode(76, "Kazakhstan"));
        expectedCodes.add(new CountryCode(77, "Kazakhstan"));
        expectedCodes.add(new CountryCode(3906698, "Vatican City State (Holy See)"));
        expectedCodes.add(new CountryCode(379, "Vatican City State (Holy See)"));
    }

    @Test
    @Order(1)
    @DisplayName("Test for SourceCodeDao: verify DocumentParserServiceImpl.isProperTable()")
    public void testTableVerification() throws Exception {
        File in = new File(PROPER_PAGE);
        Document doc = Jsoup.parse(in, null);
        DocumentParserServiceImpl parser = new DocumentParserServiceImpl();

        assertTrue(parser.isProperTable(doc.select(DocumentParserServiceImpl.TABLE_TAG_NAME).get(0)), "DocumentParserServiceImpl failed document validation");
    }

    @Test
    @Order(2)
    @DisplayName("Test for SourceCodeDao: verify DocumentParserServiceImpl.getPhoneCodesAndCountries()")
    public void testParser() throws Exception {
        File in = new File(PROPER_PAGE);
        Document doc = Jsoup.parse(in, null);
        DocumentParserServiceImpl parser = new DocumentParserServiceImpl();
        List<CountryCode> codes = new ArrayList<>();
        Executable executable = () -> codes.addAll(parser.parsePhoneCodesAndCountries(doc));

        assertDoesNotThrow(executable);

        Comparator<CountryCode> comparator = (codeOne, codeTwo) -> {
            int countriesComparation = codeOne.getCountry().compareTo(codeTwo.getCountry());
            if (countriesComparation != 0) {
                return countriesComparation;
            };
            return codeOne.getCode() - codeTwo.getCode();
        };

        expectedCodes.sort(comparator);
        codes.sort(comparator);
        assertIterableEquals(expectedCodes, codes, "DocumentParserServiceImpl failed document parsing");
     }

    @Test
    @Order(3)
    @DisplayName("Test for SourceCodeDao:test uploading from wikipedia")
    public void testDocumentUploading() {
        DocumentParserService parser = new DocumentParserServiceImpl();
        SourceDocumentDao dao = new SourceDocumentDaoImpl();

        Executable executable = () -> parser.parsePhoneCodesAndCountries(dao.uploadDocument(sourceUrl));
        assertDoesNotThrow(executable);
    }

    @Configuration
    @PropertySource("classpath:application-test.properties")
    static class Config {
    }
}
