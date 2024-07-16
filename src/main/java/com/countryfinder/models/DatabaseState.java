package com.countryfinder.models;

import com.countryfinder.models.enums.DbStateName;
import jakarta.persistence.*;

@Entity
@Table(name="database_state")
public class DatabaseState {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name="db_state_name")
    DbStateName stateName;

    @Column(name="state_value")
    String stateValue;

    public DatabaseState(DbStateName stateName, String stateValue) {
        this.stateName = stateName;
        this.stateValue = stateValue;
    }

    public DatabaseState() {
    }

    public DbStateName getStateName() {
        return stateName;
    }

    public String getStateValue() {
        return stateValue;
    }
}
