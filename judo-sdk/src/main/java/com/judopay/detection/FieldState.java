package com.judopay.detection;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class FieldState implements Parcelable {

    private ArrayList<FieldSession> sessions;
    private FieldSession currentSession;

    public FieldState() {
        this.sessions = new ArrayList<>();
        this.currentSession = new FieldSession();
    }

    public ArrayList<FieldSession> getSessions() {
        return sessions;
    }

    public FieldSession getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(FieldSession currentSession) {
        this.currentSession = currentSession;

        if (currentSession != null && !sessions.isEmpty()) {
            FieldSession last = sessions.get(sessions.size() - 1);
            this.currentSession.setValid(last.isValid());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.sessions);
        dest.writeParcelable(this.currentSession, flags);
    }

    protected FieldState(Parcel in) {
        this.sessions = in.createTypedArrayList(FieldSession.CREATOR);
        this.currentSession = in.readParcelable(FieldSession.class.getClassLoader());
    }

    public static final Parcelable.Creator<FieldState> CREATOR = new Parcelable.Creator<FieldState>() {
        @Override
        public FieldState createFromParcel(Parcel source) {
            return new FieldState(source);
        }

        @Override
        public FieldState[] newArray(int size) {
            return new FieldState[size];
        }
    };
}
