package com.judopay.detection;

import android.os.Parcel;
import android.os.Parcelable;

import com.judopay.view.PasteListenable;
import com.judopay.view.PasteListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PastedFieldsDetector implements Parcelable {

    private ConcurrentHashMap<String, ArrayList<Long>> pasteTimings;

    private PastedFieldsDetector(Map<String, PasteListenable> fields) {
        pasteTimings = new ConcurrentHashMap<>();

        for (final Map.Entry<String, PasteListenable> field : fields.entrySet()) {
            field.getValue().setPasteListener(new PasteListener() {
                @Override
                public void onPaste() {
                    if (!pasteTimings.containsKey(field.getKey())) {
                        pasteTimings.put(field.getKey(), new ArrayList<Long>());
                    }
                    pasteTimings.get(field.getKey()).add(System.currentTimeMillis());
                }
            });
        }
    }

    public void setPasteTimings(HashMap<String, ArrayList<Long>> pasteTimings) {
        this.pasteTimings = new ConcurrentHashMap<>(pasteTimings);
    }

    public ConcurrentHashMap<String, ArrayList<Long>> getPasteTimings() {
        return pasteTimings;
    }

    public static class Builder {

        private Map<String, PasteListenable> fields;

        public Builder() {
            this.fields = new HashMap<>();
        }

        public Builder add(String fieldName, PasteListenable editText) {
            fields.put(fieldName, editText);
            return this;
        }

        public PastedFieldsDetector build() {
            return new PastedFieldsDetector(fields);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.pasteTimings);
    }

    protected PastedFieldsDetector(Parcel in) {
        this.pasteTimings = (ConcurrentHashMap<String, ArrayList<Long>>) in.readSerializable();
    }

    public static final Parcelable.Creator<PastedFieldsDetector> CREATOR = new Parcelable.Creator<PastedFieldsDetector>() {
        @Override
        public PastedFieldsDetector createFromParcel(Parcel source) {
            return new PastedFieldsDetector(source);
        }

        @Override
        public PastedFieldsDetector[] newArray(int size) {
            return new PastedFieldsDetector[size];
        }
    };
}