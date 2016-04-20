package com.judopay.samples.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.judopay.Judo;
import com.judopay.model.Currency;
import com.judopay.samples.MainActivity;
import com.judopay.samples.R;
import com.judopay.samples.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SettingsPrefs settingsPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        settingsPrefs = new SettingsPrefs(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setSettingsViewModel(new SettingsViewModel(settingsPrefs.isAvsEnabled(), settingsPrefs.isAmexEnabled(), settingsPrefs.isMaestroEnabled()));

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
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Currency.currencyNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.currencySpinner.setAdapter(adapter);
        binding.currencySpinner.setSelection(getCurrencySelection());

        binding.currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currency = Currency.currencyCodes().get(position);
                saveCurrency(currency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.avsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsPrefs.setAvsEnabled(isChecked);
                setupJudo();
            }
        });

        binding.maestroSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsPrefs.setMaestroEnabled(isChecked);
                setupJudo();
            }
        });

        binding.amexSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsPrefs.setAmexEnabled(isChecked);
                setupJudo();
            }
        });
    }

    private void setupJudo() {
        Judo.setAmexEnabled(settingsPrefs.isAmexEnabled());
        Judo.setAvsEnabled(settingsPrefs.isAvsEnabled());
        Judo.setMaestroEnabled(settingsPrefs.isMaestroEnabled());
    }

    private void saveCurrency(String currency) {
        getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(MainActivity.CURRENCY_KEY, currency)
                .apply();
    }

    private int getCurrencySelection() {
        String currency = getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE)
                .getString(MainActivity.CURRENCY_KEY, Currency.GBP);

        return Currency.currencyCodes().indexOf(currency);
    }
}
