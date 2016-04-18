package com.judopay.samples.settings;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.judopay.samples.BR;

public class SettingsViewModel extends BaseObservable {

    @Bindable
    private boolean avsEnabled;

    @Bindable
    private boolean amexEnabled;

    @Bindable
    private boolean maestroEnabled;

    public SettingsViewModel(boolean avsEnabled, boolean amexEnabled, boolean maestroEnabled) {
        this.avsEnabled = avsEnabled;
        this.amexEnabled = amexEnabled;
        this.maestroEnabled = maestroEnabled;
    }

    public void setAvsEnabled(boolean avsEnabled) {
        notifyPropertyChanged(BR.avsEnabled);
        this.avsEnabled = avsEnabled;
    }

    public void setAmexEnabled(boolean amexEnabled) {
        notifyPropertyChanged(BR.amexEnabled);
        this.amexEnabled = amexEnabled;
    }

    public void setMaestroEnabled(boolean maestroEnabled) {
        notifyPropertyChanged(BR.maestroEnabled);
        this.maestroEnabled = maestroEnabled;
    }

    public boolean isMaestroEnabled() {
        return maestroEnabled;
    }

    public boolean isAvsEnabled() {
        return avsEnabled;
    }

    public boolean isAmexEnabled() {
        return amexEnabled;
    }
}
