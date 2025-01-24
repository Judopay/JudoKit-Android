package com.judopay.judokit.android.ui.cardentry;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;

// Class from NFC library that had to overridden, as it crashes on Android 12+ without FLAG_MUTABLE set.
public class NfcCardManagerImproved {

    private final NfcAdapter nfcAdapter;
    private final PendingIntent pendingIntent;
    private final Activity activity;

    private static final IntentFilter[] INTENT_FILTER = new IntentFilter[] {
        new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
        new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};
    private static final String[][] TECH_LIST = new String[][] { {
        NfcA.class.getName(), NfcB.class.getName(), IsoDep.class.getName() } };

    public NfcCardManagerImproved(final Activity activity) {
        this.activity = activity;
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        pendingIntent = PendingIntent.getActivity(activity, 0,
            new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
    }

    public void disableDispatch() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(activity);
        }
    }

    public void enableDispatch() {
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(activity, pendingIntent, INTENT_FILTER, TECH_LIST);
        }
    }
}
