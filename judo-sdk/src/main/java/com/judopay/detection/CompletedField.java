package com.judopay.detection;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class CompletedField implements Parcelable, Comparable<CompletedField> {

    private final String field;
    private final ArrayList<FieldSession> fieldTimings;

    public CompletedField(String field, ArrayList<FieldSession> fieldTimings) {
        this.field = field;
        this.fieldTimings = fieldTimings;
    }

    public String getField() {
        return field;
    }

    public List<FieldSession> getFieldTimings() {
        return fieldTimings;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.field);
        dest.writeTypedList(this.fieldTimings);
    }

    protected CompletedField(Parcel in) {
        this.field = in.readString();
        this.fieldTimings = in.createTypedArrayList(FieldSession.CREATOR);
    }

    public static final Parcelable.Creator<CompletedField> CREATOR = new Parcelable.Creator<CompletedField>() {
        @Override
        public CompletedField createFromParcel(Parcel source) {
            return new CompletedField(source);
        }

        @Override
        public CompletedField[] newArray(int size) {
            return new CompletedField[size];
        }
    };

    @Override
    public int compareTo(CompletedField completedField) {
        if (this.fieldTimings.isEmpty() && completedField.fieldTimings.isEmpty()) {
            return this.field.compareTo(completedField.field);
        }

        return this.fieldTimings.get(0).getTimeStarted().compareTo(completedField.fieldTimings.get(0).getTimeStarted());
    }
}