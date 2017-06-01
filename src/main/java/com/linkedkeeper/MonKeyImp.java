package com.linkedkeeper;

public class MonKeyImp implements MonKey {

    private final String summaryLabel;
    private Object details;
    private final String units;

    public MonKeyImp(String summaryLabel, String units) {
        this(summaryLabel, summaryLabel, units);
    }

    /**
     * Object details can be an Object[], a Collection, or a Java Object.
     */
    public MonKeyImp(String summaryLabel, Object details, String units) {
        this.summaryLabel = (summaryLabel == null) ? "" : summaryLabel;
        this.details = details;
        this.units = (units == null) ? "" : units;
    }

    /**
     * Returns the label for the monitor
     */
    public String getLabel() {
        return summaryLabel;
    }

    /**
     * Returns the units for the monitor
     */
    public String getUnits() {
        return units;
    }

    public Object getDetails() {
        return details;
    }

    @Override
    public String getRangeKey() {
        return getUnits();
    }

    @Override
    public int getSize() {
        return summaryLabel.length();
    }

    public Object getValue(String key) {
        if (LABEL_HEADER.equalsIgnoreCase(key))
            return getLabel();
        else if (UNITS_HEADER.equalsIgnoreCase(key))
            return getUnits();
        else if (DETAILS_HEADER.equalsIgnoreCase(key))
            return getDetails();
        else
            return null;
    }

    @Override
    public boolean equals(Object compareKey) {
        return (compareKey instanceof MonKeyImp &&
                summaryLabel.equals(((MonKeyImp) compareKey).summaryLabel) &&
                units.equals(((MonKeyImp) compareKey).units));
    }

    /**
     * Used when key is put into a Map to look up the monitor
     */
    @Override
    public int hashCode() {
        return (summaryLabel.hashCode() + units.hashCode());
    }

}
