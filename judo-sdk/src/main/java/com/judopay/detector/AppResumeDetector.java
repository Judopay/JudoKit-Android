package com.judopay.detector;

import java.util.ArrayList;

public class AppResumeDetector {

    private ArrayList<Long> resumedTimings;
    private int pauseCount;

    public AppResumeDetector() {
        this.resumedTimings = new ArrayList<>();
    }

    public void onPause() {
        pauseCount++;
    }

    public void onResume() {
        if(pauseCount > resumedTimings.size()) {
            resumedTimings.add(System.currentTimeMillis());
        }
    }

    public ArrayList<Long> getResumedTimings() {
        return resumedTimings;
    }

    public int getPauseCount() {
        return pauseCount;
    }

    public void setResumedTimings(ArrayList<Long> resumedTimings) {
        this.resumedTimings = resumedTimings;
    }

    public void setPauseCount(int pauseCount) {
        this.pauseCount = pauseCount;
    }
}
