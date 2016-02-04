package com.judopay.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.judopay.R;

public class Dialogs {

    public static AlertDialog createDeclinedPaymentDialog(Context context) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.payment_failed)
                .setMessage(R.string.please_check_details_try_again)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    public static AlertDialog createConnectionErrorDialog(Context context) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.connection_error)
                .setMessage(R.string.please_check_your_internet_connection)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

}