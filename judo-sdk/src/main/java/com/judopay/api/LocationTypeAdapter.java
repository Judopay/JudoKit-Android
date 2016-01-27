package com.judopay.api;

import android.content.Context;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.judopay.model.Location;
import com.judopay.shield.LocationService;

import java.io.IOException;

public class LocationTypeAdapter extends TypeAdapter<Location> {

    private static final String LATITUDE_PROPERTY = "latitude";
    private static final String LONGITUDE_PROPERTY = "longitude";

    private final Context context;

    public LocationTypeAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void write(JsonWriter out, Location value) throws IOException {
        if (value == null) {
            android.location.Location location = LocationService.getLocation(this.context);

            if (location != null) {
                writeLatLng(out, location.getLatitude(), location.getLongitude());
            } else {
                out.nullValue();
            }
        } else {
            writeLatLng(out, value.getLatitude(), value.getLongitude());
        }
    }

    protected void writeLatLng(JsonWriter out, double latitude, double longitude) throws IOException {
        out.beginObject();

        out.name(LATITUDE_PROPERTY);
        out.value(latitude);

        out.name(LONGITUDE_PROPERTY);
        out.value(longitude);

        out.endObject();
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        return null;
    }
}