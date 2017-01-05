package com.judopay.detection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class UserAnalytics {

    private int totalKeystrokes;
    private ArrayList<Date> appResumed;
    private ArrayList<FieldMetaData> fieldMetaData;

    private UserAnalytics(int totalKeystrokes, ArrayList<Date> appResumed, ArrayList<FieldMetaData> fieldMetaData) {
        this.totalKeystrokes = totalKeystrokes;
        this.appResumed = appResumed;
        this.fieldMetaData = fieldMetaData;
    }

    public int getTotalKeystrokes() {
        return totalKeystrokes;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("appResumed", appResumed);
        map.put("totalKeystrokes", totalKeystrokes);
        map.put("fieldMetadata", fieldMetaData);

        return Collections.unmodifiableMap(map);
    }

    public Collection<Date> getAppResumed() {
        return appResumed;
    }

    public Collection<FieldMetaData> getFieldMetaData() {
        return fieldMetaData;
    }

    public static final class Builder {

        private int totalKeystrokes;
        private ArrayList<Long> appResumed;
        private ArrayList<CompletedField> completedFields;
        private Map<String, ArrayList<Long>> pastedFields;

        public Builder setTotalKeystrokes(int totalKeystrokes) {
            this.totalKeystrokes = totalKeystrokes;
            return this;
        }

        public Builder setAppResumed(ArrayList<Long> appResumed) {
            this.appResumed = appResumed;
            return this;
        }

        public Builder setCompletedFields(ArrayList<CompletedField> completedFields) {
            this.completedFields = completedFields;
            return this;
        }

        public Builder setPastedFields(Map<String, ArrayList<Long>> pastedFields) {
            this.pastedFields = pastedFields;
            return this;
        }

        public UserAnalytics build() {
            ArrayList<FieldMetaData> fieldMetaData = new ArrayList<>();

            for (CompletedField completedField : completedFields) {
                FieldMetaData.Builder builder = new FieldMetaData.Builder()
                        .setField(completedField.getField())
                        .setSessions(completedField.getFieldTimings());

                if (pastedFields.containsKey(completedField.getField())) {
                    List<Long> times = pastedFields.get(completedField.getField());
                    List<Date> dates = new ArrayList<>();

                    for (Long time : times) {
                        Date date = new Date();
                        date.setTime(time);
                        dates.add(date);
                    }
                    builder.setPasted(dates);
                }

                fieldMetaData.add(builder.build());
            }

            ArrayList<Date> appResumedDates = new ArrayList<>();

            for (Long time : appResumed) {
                Date date = new Date();
                date.setTime(time);
                appResumedDates.add(date);
            }

            return new UserAnalytics(totalKeystrokes, appResumedDates, fieldMetaData);
        }
    }
}