package com.judopay.detector;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class TotalKeystrokesDetector implements Parcelable {

    private int totalKeystrokes;

    private TotalKeystrokesDetector(Map<String, EditText> fields) {
        for (Map.Entry<String, EditText> entry : fields.entrySet()) {
            entry.getValue().addTextChangedListener(new TextWatcher() {

                private int beforeCount;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    this.beforeCount = count;
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(count != 0 || count < this.beforeCount) {
                        totalKeystrokes++;
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) { }
            });
        }
    }

    public int getTotalKeystrokes() {
        return totalKeystrokes;
    }

    public void setTotalKeystrokes(int totalKeystrokes) {
        this.totalKeystrokes = totalKeystrokes;
    }

    public static class Builder {

        private Map<String, EditText> fields;

        public Builder() {
            fields = new HashMap<>();
        }

        public Builder add(String fieldName, EditText editText) {
            fields.put(fieldName, editText);
            return this;
        }

        public TotalKeystrokesDetector build() {
            return new TotalKeystrokesDetector(fields);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.totalKeystrokes);
    }

    protected TotalKeystrokesDetector(Parcel in) {
        this.totalKeystrokes = in.readInt();
    }

    public static final Parcelable.Creator<TotalKeystrokesDetector> CREATOR = new Parcelable.Creator<TotalKeystrokesDetector>() {
        @Override
        public TotalKeystrokesDetector createFromParcel(Parcel source) {
            return new TotalKeystrokesDetector(source);
        }

        @Override
        public TotalKeystrokesDetector[] newArray(int size) {
            return new TotalKeystrokesDetector[size];
        }
    };
}