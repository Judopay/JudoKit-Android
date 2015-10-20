package com.judopay.customer;

public class Country {

    public static final String UNITED_KINGDOM = "United Kingdom";
    public static final String UNITED_STATES = "United States";
    public static final String CANADA = "Canada";
    public static final String OTHER = "Other";

    private final int code;
    private final String displayName;

    public Country(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

}