package com.countryfinder.services;


import com.countryfinder.models.CountryCode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentParserServiceImpl implements DocumentParserService {
    public static final String TABLE_TAG_NAME = "table";
    public static final String  ROW_TAG_NAME = "tr";
    public static final String HEADER_CELL_TAG_NAME = "th";
    public static final int COUNTRY_CELL = 0;
    public static final int CODE_CELL = 1;
    public static final String CELL_TAG_NAME = "td";

    //first should be several digits, then round brackets with one or several
    //numbers, divided by space and ,
    final String CODE_COLUMN_NUMBER_PATTERN = "\\d+ ?(\\(\\d+(, +\\d+)*\\))?";
    final String CODE_COLUMN_COMMON_PATTERN = "[\\d a-zA-z,()]*";
    Logger LOG = LoggerFactory.getLogger(DocumentParserServiceImpl.class);

    @Override
    public List<CountryCode> getPhoneCodesAndCountries(Document document) throws Exception {
        if (document == null) {
            LOG.error("Document is null");
            throw new Exception("Document is null");
        }

        Element table = findPhoneCodesTable(document);

        if (table == null) {
            LOG.error("Expected table has not found. Possibly, document has unexpected structure");
            throw new Exception("Expected table has not found. Possibly, document has unexpected structure");
        }
        return extractDataFromTable(table);
    }

    private Element findPhoneCodesTable(Document document) {
        Elements tables = document.select(TABLE_TAG_NAME);
        for (Element table : tables) {
            if (isProperTable(table)) {
                return table;
            }
        }
        return null;
    }

    //let's make sure that provided html page element is a table
    //with expected structure and proper data
    public boolean isProperTable(Element element) {
        if (!TABLE_TAG_NAME.equalsIgnoreCase(element.tagName())) {
            return false;
        }

        Elements rows = element.select(ROW_TAG_NAME);

        //check table's header
        if(!checkZeroRow(rows.get(0)) ||!checkFirstRow(rows.get(1))) {
            return false;
        }

        //check rows with table's data
        for (int i = 2; i < rows.size(); i++) {
            Element row = rows.get(i);
            if (!checkNonHeaderRow(row)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkNonHeaderRow(Element element) {

        // Any latin letter suits us, including embedded symbols like ô, Å
        final String COUNTRY_COLUMN_PATTERN = "[\\p{IsLatin} ',()-]*";
        final int TOTAL_CELLS_NUMBER = 4;

        if(element == null || !ROW_TAG_NAME.equals(element.tagName())) {
            return false;
        }

        Elements cells = element.select(CELL_TAG_NAME);

        if(cells.size() != TOTAL_CELLS_NUMBER) {
            return false;
        }

        Element countryCell = cells.get(COUNTRY_CELL);

        if(!checkPattern(countryCell, COUNTRY_COLUMN_PATTERN, true) ) {
            return false;
        }

        Element codeCell = cells.get(CODE_CELL);

        if(!checkPattern(codeCell, CODE_COLUMN_COMMON_PATTERN, true)
                || !checkPattern(codeCell, CODE_COLUMN_NUMBER_PATTERN, false)) {
            return false;
        }
        return true;
    }

    private boolean checkZeroRow(Element row) {
        final String SERVING = "Serving";
        final String CODE = "Code";
        final String TIME = "Time (UTC ±)";
        final int TOTAL_CELLS_NUMBER = 3;
        final int SERVING_CELL = 0;
        final int CODE_CELL = 1;
        final int TIME_CELL = 2;

        if (row == null || !ROW_TAG_NAME.equalsIgnoreCase(row.tagName())) {
            return false;
        }

        Elements firstTableRowCells = row.select(HEADER_CELL_TAG_NAME);

        if (firstTableRowCells.size() != TOTAL_CELLS_NUMBER) {
            return false;
        }
        if (!isTagTextEquals(firstTableRowCells.get(SERVING_CELL), SERVING)) {
            return false;
        }
        if (!isTagTextEquals(firstTableRowCells.get(CODE_CELL), CODE)) {
            return false;
        }
        if (!isTagTextEquals(firstTableRowCells.get(TIME_CELL), TIME)) {
            return false;
        }
        return true;
    }

    private boolean checkFirstRow(Element row) {
        final String ZONE = "Zone";
        final String DST = "DST";
        final int TOTAL_CELLS_NUMBER = 2;
        final int ZONE_CELL = 0;
        final int DST_CELL = 1;

        if (row == null || !ROW_TAG_NAME.equals(row.tagName())) {
            return false;
        }

        Elements secondTableRowCells = row.select(HEADER_CELL_TAG_NAME);

        if (secondTableRowCells.size() != TOTAL_CELLS_NUMBER) {
            return false;
        }
        if (!isTagTextEquals(secondTableRowCells.get(ZONE_CELL), ZONE)) {
            return false;
        }
        if (!isTagTextEquals(secondTableRowCells.get(DST_CELL), DST)) {
            return false;
        }
        return true;
    }

    private boolean checkPattern(Element element, String pattern, boolean entireStringMatches) {
        if (element == null || element.text() == null) {
            return false;
        }

        String text = element.text();

        if (entireStringMatches) {
            return text.matches(pattern);
        } else {
            Pattern patternObj = Pattern.compile(pattern);
            Matcher matcher = patternObj.matcher(text);
            return matcher.find();
        }
    }

    private boolean isTagTextEquals(Element element, String expectedText) {
        String text = element.text();

        if (element == null || text == null) {
            return false;
        }
        return text.equals(expectedText);
    }

    private List<CountryCode> extractDataFromTable(Element table) {
        List<CountryCode> countries = new ArrayList<>();

        Elements rows = table.select(ROW_TAG_NAME);
        for(int i = 2; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cells = row.select(CELL_TAG_NAME);
            String country = parseCountry(cells.get(COUNTRY_CELL).text());
            List<String> oneCountryCodes = parseCode(cells.get(CODE_CELL).text());
            countries.addAll(createCountryCodesList(oneCountryCodes, country));
        }
        return countries;
    }

    private String parseCountry(String countryCellValue) {
        return countryCellValue.strip();
    }

    private List<String> parseCode(String codeCellValue) {
        final String DIGITS_WITH_BRACKETS = "\\d+ ?(\\(\\d+(, +\\d+)*\\))";
        final String ONLY_DIGITS = "\\d+";
        final Pattern numberWithBracketsPattern = Pattern.compile(DIGITS_WITH_BRACKETS);
        final Pattern onlyDigitsPattern = Pattern.compile(ONLY_DIGITS);
        Matcher matcher = numberWithBracketsPattern.matcher(codeCellValue);
        final List<String> parsedCodes = new ArrayList<>();

        //first lets find codes which use brackets
        // like 7(980) or 1(3,4,7)
        while(matcher.find()) {
            String codeFromCell = matcher.group();
            int firstBracketIndex = codeFromCell.indexOf('(');
            int secondBracketIndex = codeFromCell.indexOf(')');

            //extract
            String digitBeforeBracket = codeFromCell.substring(0, firstBracketIndex).trim();
            String[] digitsInsideBrackets = codeFromCell.substring(firstBracketIndex + 1, secondBracketIndex)
                    .split(" *,");
            for(String digit : digitsInsideBrackets) {
                parsedCodes.add(digitBeforeBracket.trim()+digit.trim());
            }
        }
        codeCellValue = codeCellValue.replaceAll(DIGITS_WITH_BRACKETS, "");
        matcher = onlyDigitsPattern.matcher(codeCellValue);
        while(matcher.find()) {
           parsedCodes.add(matcher.group().trim());
        }
        return parsedCodes;
    }

    private List<CountryCode> createCountryCodesList(List<String> codeStrings, String country) {
        List<CountryCode> result = new ArrayList<>();

        for(String codeString : codeStrings) {
            result.add(new CountryCode(Integer.parseInt(codeString), country));
        }
        return result;
    }
}
