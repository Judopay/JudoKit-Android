package com.judopay.detection;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

@SuppressWarnings({"FieldCanBeLocal", "unused", "WeakerAccess"})
public class FieldSession implements Parcelable {

    private boolean valid;
    private Date timeStarted;
    private Date timeEnded;
    private Date timeEdited;

    public Date getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(Date timeStarted) {
        this.timeStarted = timeStarted;
    }

    public Date getTimeEnded() {
        return timeEnded;
    }

    public void setTimeEnded(Date timeEnded) {
        this.timeEnded = timeEnded;
    }

    public Date getTimeEdited() {
        return timeEdited;
    }

    public void setTimeEdited(Date timeEdited) {
        if (this.timeEdited == null) {
            this.timeEdited = timeEdited;
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.valid ? (byte) 1 : (byte) 0);
        dest.writeLong(this.timeStarted != null ? this.timeStarted.getTime() : -1);
        dest.writeLong(this.timeEnded != null ? this.timeEnded.getTime() : -1);
        dest.writeLong(this.timeEdited != null ? this.timeEdited.getTime() : -1);
    }

    public FieldSession() {
    }

    protected FieldSession(Parcel in) {
        this.valid = in.readByte() != 0;
        long tmpTimeStarted = in.readLong();
        this.timeStarted = tmpTimeStarted == -1 ? null : new Date(tmpTimeStarted);
        long tmpTimeEnded = in.readLong();
        this.timeEnded = tmpTimeEnded == -1 ? null : new Date(tmpTimeEnded);
        long tmpTimeEdited = in.readLong();
        this.timeEdited = tmpTimeEdited == -1 ? null : new Date(tmpTimeEdited);
    }

    public static final Parcelable.Creator<FieldSession> CREATOR = new Parcelable.Creator<FieldSession>() {
        @Override
        public FieldSession createFromParcel(Parcel source) {
            return new FieldSession(source);
        }

        @Override
        public FieldSession[] newArray(int size) {
            return new FieldSession[size];
        }
    };
}
