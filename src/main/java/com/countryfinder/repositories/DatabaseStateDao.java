package com.countryfinder.repositories;

import com.countryfinder.models.DatabaseState;
import com.countryfinder.models.enums.DbStateName;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseStateDao extends CrudRepository<DatabaseState,DbStateName> {

}

