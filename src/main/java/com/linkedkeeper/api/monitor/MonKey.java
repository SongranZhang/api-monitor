package com.linkedkeeper.api.monitor;

public interface MonKey {

    String LABEL_HEADER = "Label";
    String UNITS_HEADER = "Units";
    String DETAILS_HEADER = "Details";

    Object getValue(String primaryKey);

}
