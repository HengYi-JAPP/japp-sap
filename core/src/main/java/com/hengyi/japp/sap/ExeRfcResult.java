package com.hengyi.japp.sap;

import java.io.Serializable;
import java.util.Map;

public class ExeRfcResult implements Serializable {
    private Map<String, Object> exports;
    private Map<String, Object> changings;
    private Map<String, Object> tables;

    public Map<String, Object> getExports() {
        return exports;
    }

    public void setExports(Map<String, Object> exports) {
        this.exports = exports;
    }

    public Map<String, Object> getChangings() {
        return changings;
    }

    public void setChangings(Map<String, Object> changings) {
        this.changings = changings;
    }

    public Map<String, Object> getTables() {
        return tables;
    }

    public void setTables(Map<String, Object> tables) {
        this.tables = tables;
    }
}
