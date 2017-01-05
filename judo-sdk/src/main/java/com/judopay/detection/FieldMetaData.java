package com.judopay.detection;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class FieldMetaData {

    private String field;
    private List<FieldSession> sessions;
    private List<Date> pasted;

    private FieldMetaData(String field, List<FieldSession> sessions, List<Date> pasted) {
        this.field = field;
        this.sessions = sessions;
        this.pasted = pasted;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("fieldMetadata", this);

        return map;
    }

    public static class Builder {

        private String field;
        private List<FieldSession> sessions;
        private List<Date> pasted;

        public Builder setField(String field) {
            this.field = field;
            return this;
        }

        public Builder setSessions(List<FieldSession> sessions) {
            this.sessions = sessions;
            return this;
        }

        public Builder setPasted(List<Date> pasted) {
            this.pasted = pasted;
            return this;
        }

        public FieldMetaData build() {
            return new FieldMetaData(field, sessions, pasted);
        }
    }
}