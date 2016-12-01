package com.judopay.model;

/**
 * The GPS co-ordinates of a location.
 */
@SuppressWarnings("unused")
public final class Location {

    private Double latitude;
    private Double longitude;

    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

}