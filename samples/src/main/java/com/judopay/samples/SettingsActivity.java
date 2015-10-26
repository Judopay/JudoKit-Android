package com.judopay.samples;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.judopay.JudoPay;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @Bind(R.id.avs_switch)
    SwitchCompat avsSwitch;

    @Bind(R.id.ssl_pinning_switch)
    SwitchCompat sslPinningSwitch;

    @Bind(R.id.maestro_switch)
    SwitchCompat maestroSwitch;

    @Bind(R.id.amex_switch)
    SwitchCompat amexSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        initialise();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initialise() {
        avsSwitch.setChecked(JudoPay.isAvsEnabled());
        sslPinningSwitch.setChecked(JudoPay.isSslPinningEnabled());
        maestroSwitch.setChecked(JudoPay.isMaestroEnabled());
        amexSwitch.setChecked(JudoPay.isAmexEnabled());

        avsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JudoPay.setAvsEnabled(isChecked);
            }
        });

        sslPinningSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JudoPay.setSslPinningEnabled(isChecked);
            }
        });

        maestroSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JudoPay.setMaestroEnabled(isChecked);
            }
        });

        amexSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JudoPay.setAmexEnabled(isChecked);
            }
        });
    }
}
