package com.countryfinder.repositories;

import com.countryfinder.models.CountryCode;
import com.countryfinder.models.CountryCodeId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryCodeDao extends CrudRepository<CountryCode, CountryCodeId> {


    // first search for all codes which are first digits of the number
    // for example codes 1 and 12 can be first digits of 12345
    // then we return max length of those codes
    String queryMax = "SELECT MAX(FUNCTION('LENGTH', CAST(c.code AS string))) FROM CountryCode c " +
            "WHERE FUNCTION('SUBSTRING', :number, 1, FUNCTION('LENGTH', CAST(c.code AS string))) = CAST(c.code AS string)";

    @Query(queryMax)
    Integer searchMaxCodeLength(@Param("number") String number);

    // then we again search for all codes which are first digits of the number
    // but choose only codes with length == max
    // for example codes 7, 7840 are suitable for number 784099
    // but only 7840 will be chosen, because it has biggest length
    String queryCode = "SELECT c FROM CountryCode c WHERE FUNCTION('LENGTH', CAST(c.code AS string)) = :max" +
            " AND FUNCTION('SUBSTRING', :number, 1, FUNCTION('LENGTH', CAST(c.code AS string))) = CAST(c.code AS string)";

    @Query(queryCode)
    List<CountryCode> searchCodeByPhoneNumberAndLength(@Param("max") Integer length, @Param("number") String number);
}
