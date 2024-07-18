package com.countryfinder.services;

import com.countryfinder.models.CountryCode;
import com.countryfinder.repositories.CountryCodeDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class CountryCodeServiceImplTests {

    @Mock
    private CountryCodeDao codesDao;

    @InjectMocks
    private CountryCodeServiceImpl countryCodeService;

    private final String PHONE_NUMBER = "845435435334353";
    private final int MAX = 1;


    @Test
    @DisplayName("CountryCodeService: getSuitableCountryCodes")
    public void testGetSuitableCountryCodes() {
        final int code = Integer.parseInt(PHONE_NUMBER.substring(0,1));
        final String EMBEDDED_PHONE_NUMBER = "+" + PHONE_NUMBER + " ";
        final List<CountryCode> codes = new ArrayList<>();

        codes.add(new CountryCode(code, "Rohan"));
        when(codesDao.searchMaxCodeLength(PHONE_NUMBER)).thenReturn(MAX);
        when(codesDao.searchCodeByPhoneNumberAndLength(MAX, PHONE_NUMBER)).thenReturn(codes);

        final String equalsTestMessage = "getSuitableCountryCodes did not return any suitable country codes we expect";
        assertIterableEquals(codes, countryCodeService.getSuitableCountryCodes(PHONE_NUMBER), equalsTestMessage) ;

        Executable executable = () -> countryCodeService.getSuitableCountryCodes(EMBEDDED_PHONE_NUMBER);

        final String throwTestMessage = "getSuitableCountryCodes thrown an error. phone number is " + EMBEDDED_PHONE_NUMBER;
        assertDoesNotThrow(executable, throwTestMessage);
    }

    @Test
    @DisplayName("CountryCodeService: getSuitableCountryCodes (must return empty list when dao sends null)")
    public void testGetSuitableCountryCodesNullCase() {
        List<CountryCode> emptyList = List.of();

        when(codesDao.searchMaxCodeLength(PHONE_NUMBER)).thenReturn(null);
        assertIterableEquals(emptyList, countryCodeService.getSuitableCountryCodes(PHONE_NUMBER));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("CountryCodeService: getSuitableCountryCodes (must throw an error when phone number is corrupted)")
    public void testGetSuitableCountryCodesIncorrectNumberCase() {

        final List<CountryCode> codes = new ArrayList<>();

        // let's configure dao to make sure that it doesn't
        // throw an exception
        when(codesDao.searchMaxCodeLength(PHONE_NUMBER)).thenReturn(MAX);
        when(codesDao.searchCodeByPhoneNumberAndLength(MAX, PHONE_NUMBER)).thenReturn(codes);

        Executable space = () -> countryCodeService.getSuitableCountryCodes("1  225");
        Executable letter = () -> countryCodeService.getSuitableCountryCodes("A1222");
        Executable oneDigit = () -> countryCodeService.getSuitableCountryCodes("1");
        Executable empty = () -> countryCodeService.getSuitableCountryCodes("");

        assertThrows(RuntimeException.class,space);
        assertThrows(RuntimeException.class,letter);
        assertThrows(RuntimeException.class,oneDigit);
        assertThrows(RuntimeException.class,empty);
    }
}
