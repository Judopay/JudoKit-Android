package com.judopay.samples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.judopay.JudoPay;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @Bind(R.id.avs_switch)
    SwitchCompat avsSwitch;

    @Bind(R.id.ssl_pinning_switch)
    SwitchCompat sslPinningSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        initialise();
    }

    private void initialise() {
        avsSwitch.setChecked(JudoPay.isAvsEnabled());

        avsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JudoPay.setAvsEnabled(isChecked);
            }
        });

        sslPinningSwitch.setChecked(JudoPay.isSslPinningEnabled());

        sslPinningSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JudoPay.setSslPinningEnabled(isChecked);
            }
        });
    }
}
