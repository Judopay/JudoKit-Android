package com.judopay.detector;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;
import android.view.View;

import com.judopay.devicedna.signal.user.CompletedField;
import com.judopay.devicedna.signal.user.FieldSession;
import com.judopay.devicedna.signal.user.FieldState;
import com.judopay.validation.Validation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import rx.Observable;
import rx.functions.Action1;

public class CompletedFieldsDetector implements Parcelable {

    private ConcurrentSkipListSet<CompletedField> completedFields;
    private ConcurrentHashMap<String, FieldState> fieldStateMap;

    private CompletedFieldsDetector(Map<String, Pair<Observable<Validation>, View>> map) {
        this.completedFields = new ConcurrentSkipListSet<>(new Comparator<CompletedField>() {
            @Override
            public int compare(CompletedField newField, CompletedField oldField) {
                return newField.getField().compareTo(oldField.getField());
            }
        });

        this.fieldStateMap = new ConcurrentHashMap<>();

        for (final Map.Entry<String, Pair<Observable<Validation>, View>> entry : map.entrySet()) {
            final String fieldName = entry.getKey();

            View view = entry.getValue().second;
            Observable<Validation> observable = entry.getValue().first;

            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    FieldState fieldState = getFieldState(fieldName);
                    FieldSession session = fieldState.getCurrentSession();

                    if (session == null) {
                        session = new FieldSession();
                        fieldState.setCurrentSession(session);
                    }

                    if (hasFocus) {
                        session.setTimeStarted(new Date());
                    } else {
                        session.setTimeEnded(new Date());
                        addCompletedField(fieldName);
                    }
                }
            });

            observable.subscribe(new Action1<Validation>() {
                @Override
                public void call(Validation validation) {
                    FieldSession session = getFieldState(fieldName).getCurrentSession();

                    if (session != null) {
                        session.setValid(validation.isValid());
                        session.setTimeEdited(new Date());

                    }
                }
            });
        }
    }

    private FieldState getFieldState(String fieldName) {
        if (!fieldStateMap.containsKey(fieldName)) {
            fieldStateMap.put(fieldName, new FieldState());
        }

        return fieldStateMap.get(fieldName);
    }

    public ArrayList<CompletedField> getCompletedFields() {
        return new ArrayList<>(completedFields);
    }

    public void setCompletedFields(ArrayList<CompletedField> completedFields) {
        this.completedFields = new ConcurrentSkipListSet<>(completedFields);
    }

    public ConcurrentHashMap<String, FieldState> getFieldStateMap() {
        return fieldStateMap;
    }

    public void setFieldStateMap(ConcurrentHashMap<String, FieldState> fieldStateMap) {
        this.fieldStateMap = fieldStateMap;
    }

    private void addCompletedField(String fieldName) {
        FieldState fieldState = fieldStateMap.get(fieldName);
        FieldSession currentFieldTiming = fieldState.getCurrentSession();

        fieldState.getSessions().add(currentFieldTiming);
        fieldState.setCurrentSession(null);

        for (CompletedField completedField : completedFields) {
            if (completedField.getField().equals(fieldName)) {
                completedFields.remove(completedField);
            }
        }

        completedFields.add(new CompletedField(fieldName, fieldState.getSessions()));
    }

    public ArrayList<CompletedField> getFieldsOrderedByCompletion() {
        return new ArrayList<>(completedFields);
    }

    public static class Builder {

        private Map<String, Pair<Observable<Validation>, View>> map;

        public Builder() {
            map = new HashMap<>();
        }

        public Builder add(String fieldName, Observable<Validation> observable, View view) {
            map.put(fieldName, new Pair<>(observable, view));
            return this;
        }

        public CompletedFieldsDetector build() {
            return new CompletedFieldsDetector(map);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.completedFields);
        dest.writeInt(this.fieldStateMap.size());
        for (Map.Entry<String, FieldState> entry : this.fieldStateMap.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    protected CompletedFieldsDetector(Parcel in) {
        this.completedFields = (ConcurrentSkipListSet<CompletedField>) in.readSerializable();
        int fieldStateMapSize = in.readInt();
        this.fieldStateMap = new ConcurrentHashMap<>(fieldStateMapSize);
        for (int i = 0; i < fieldStateMapSize; i++) {
            String key = in.readString();
            FieldState value = in.readParcelable(FieldState.class.getClassLoader());
            this.fieldStateMap.put(key, value);
        }
    }

    public static final Parcelable.Creator<CompletedFieldsDetector> CREATOR = new Parcelable.Creator<CompletedFieldsDetector>() {
        @Override
        public CompletedFieldsDetector createFromParcel(Parcel source) {
            return new CompletedFieldsDetector(source);
        }

        @Override
        public CompletedFieldsDetector[] newArray(int size) {
            return new CompletedFieldsDetector[size];
        }
    };
}