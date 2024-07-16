package com.countryfinder.util;

import com.countryfinder.models.DatabaseState;
import com.countryfinder.models.enums.DbStateName;

public class DataBaseStateFactory {
    private static final DatabaseState AVAILABLE_STATE = new DatabaseState(DbStateName.AVAILABILITY_STATUS, "AVAILABLE");
    private static final DatabaseState NOT_AVAILABLE_STATE = new DatabaseState(DbStateName.AVAILABILITY_STATUS, "NOT_AVAILABLE");

    public static DatabaseState getAvailableState() {return AVAILABLE_STATE;}
    public static DatabaseState getNotAvailableState() {return NOT_AVAILABLE_STATE;}
}
