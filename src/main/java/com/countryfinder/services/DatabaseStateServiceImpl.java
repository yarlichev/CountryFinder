package com.countryfinder.services;

import com.countryfinder.models.DatabaseState;
import com.countryfinder.models.enums.DbStateName;
import com.countryfinder.repositories.DatabaseStateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseStateServiceImpl implements DatabaseStateService{
    private final DatabaseStateDao dao;

    @Autowired
    public DatabaseStateServiceImpl(DatabaseStateDao dao) {
        this.dao = dao;
    }

    @Override
    public void saveState(DatabaseState state) {
        dao.save(state);
    }

    @Override
    public DatabaseState getAvailabilityState() {
        return dao.findById(DbStateName.AVAILABILITY_STATUS).orElse(null);
    }

    @Override
    public void removeState(DbStateName stateName) {
        dao.deleteById(stateName);
    }

    @Override
    public DatabaseState findState(DbStateName stateName) {
        return dao.findById(stateName).orElse(null);
    }

    @Override
    public void clear() {
        dao.deleteAll();
    }
}
