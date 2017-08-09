package com.novp.sprytar.util.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.novp.sprytar.R;

public class SimpleOkDialog {
    public static AlertDialog getDialog(Context c, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(message)
                .setPositiveButton(R.string.ok, (DialogInterface dialog, int which) -> {});
        return builder.create();
    }
}
