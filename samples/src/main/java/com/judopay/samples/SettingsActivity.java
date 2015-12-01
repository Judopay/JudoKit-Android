package com.judopay.samples;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.judopay.JudoPay;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @Bind(R.id.avs_switch)
    SwitchCompat avsSwitch;

    @Bind(R.id.maestro_switch)
    SwitchCompat maestroSwitch;

    @Bind(R.id.amex_switch)
    SwitchCompat amexSwitch;

    @Bind(R.id.three_d_secure_switch)
    SwitchCompat threeDSecureSwitch;

    @Bind(R.id.currency_spinner)
    Spinner currencySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.settings);

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
        maestroSwitch.setChecked(JudoPay.isMaestroEnabled());
        amexSwitch.setChecked(JudoPay.isAmexEnabled());
        threeDSecureSwitch.setChecked(JudoPay.isThreeDSecureEnabled());

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getCurrencies());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);

        currencySpinner.setSelection(getCurrencySelection());

        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveCurrency(adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        avsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JudoPay.setAvsEnabled(isChecked);
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

        threeDSecureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JudoPay.setThreeDSecureEnabled(isChecked);
            }
        });
    }

    private void saveCurrency(String currency) {
        getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(MainActivity.CURRENCY_KEY, currency)
                .apply();
    }

    private List<String> getCurrencies() {
        String[] currencies = {"GBP", "USD", "CAD"};
        return Arrays.asList(currencies);
    }

    public int getCurrencySelection() {
        String currency = getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE)
                .getString(MainActivity.CURRENCY_KEY, null);

        return getCurrencies().indexOf(currency);
    }
}
