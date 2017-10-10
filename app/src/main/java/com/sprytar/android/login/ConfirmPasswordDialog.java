package com.sprytar.android.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sprytar.android.R;
import com.sprytar.android.util.ui.SignUpEditText;

public class ConfirmPasswordDialog {

    public interface OnConfirmPasswordListener{
        void onDone(String password);
        void onCancel();
    }

    public static AlertDialog getDialog(Context context, final OnConfirmPasswordListener listener) {

        String title = context.getResources().getString(R.string.pick_user_dialog_title);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_password, null);

        final SignUpEditText password = (SignUpEditText) dialogView.findViewById(R.id.password_editText);

        TextView titleTextView = new TextView(context);
        titleTextView.setText(title);
        titleTextView.setPadding(10, 10, 10, 10);
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));
        titleTextView.setTextSize(20);

        dialogBuilder.setCustomTitle(titleTextView);

        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle(title);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(listener != null){
                    listener.onDone(password.getText().toString());
                }
            }
        });
        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(listener != null){
                    listener.onCancel();
                }
            }
        });

        return dialogBuilder.create();

    }
}
