package com.countryfinder.models;

import com.countryfinder.models.enums.DbStateName;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="database_state")
public class DatabaseState {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name="db_state_name")
    private DbStateName stateName;

    @Column(name="state_value")
    private String stateValue;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof DatabaseState) {
            return false;
        }

        DatabaseState that = (DatabaseState) o;

        return stateName == that.stateName && Objects.equals(stateValue, that.stateValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateName, stateValue);
    }
}
