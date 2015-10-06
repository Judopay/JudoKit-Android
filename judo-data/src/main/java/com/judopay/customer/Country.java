package com.judopay.customer;

public class Country {

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

    public static int getCodeFromCountry(String country) {
        switch (country) {
            case "GB":
                return 826;
            case "CA":
                return 124;
            case "US":
                return 840;
            default:
                return 0;
        }
    }

}