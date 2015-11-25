package com.judopay;

import android.os.Bundle;

public class RegisterCardActivity extends JudoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getIntent().hasExtra(JudoPay.JUDO_CONSUMER)) {
            throw new IllegalArgumentException("consumer must be provided to RegisterCardActivity");
        }

        setTitle(R.string.add_card);

        if (savedInstanceState == null) {
            RegisterCardFragment registerCardFragment = new RegisterCardFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(JudoPay.JUDO_CONSUMER, getIntent().getParcelableExtra(JudoPay.JUDO_CONSUMER));

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