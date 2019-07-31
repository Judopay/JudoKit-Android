package com.judopay.model;

import com.google.gson.annotations.SerializedName;

public class SDKInfo {
    @SerializedName("Version")
    private final String version;
    @SerializedName("Name")
    private final String name;

    public SDKInfo(final String version, final String name) {
        this.version = version;
        this.name = name;
    }
}
