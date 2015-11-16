package com.judopay.register;

import android.os.Bundle;

import com.judopay.JudoActivity;
import com.judopay.JudoPay;
import com.judopay.R;

public class RegisterCardActivity extends JudoActivity {

    public static final String EXTRA_CONSUMER = "Judo-Consumer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getIntent().hasExtra(EXTRA_CONSUMER)) {
            throw new IllegalArgumentException("consumer must be provided to RegisterCardActivity");
        }

        setTitle(R.string.add_card);

        if (savedInstanceState == null) {
            RegisterCardFragment registerCardFragment = new RegisterCardFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(RegisterCardFragment.KEY_CONSUMER, getIntent().getParcelableExtra(EXTRA_CONSUMER));

            registerCardFragment.setArguments(arguments);

            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, registerCardFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(JudoPay.RESULT_CANCELED);
    }

}