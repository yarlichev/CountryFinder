package com.countryfinder.services;

import com.countryfinder.models.DatabaseState;
import com.countryfinder.models.enums.DbStateName;

public interface DatabaseStateService {
    void clear();
    DatabaseState findState(DbStateName stateName);
    void removeState(DbStateName stateName);
    void saveState(DatabaseState state);
    DatabaseState getAvailabilltyState();
}
