package com.linkedkeeper;

public interface MonKey {

    String LABEL_HEADER = "Label";
    String UNITS_HEADER = "Units";
    String DETAILS_HEADER = "Details";

    Object getValue(String primaryKey);

    String getRangeKey();

    int getSize();

}
