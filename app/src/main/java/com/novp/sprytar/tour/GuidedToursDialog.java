package com.novp.sprytar.tour;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.novp.sprytar.R;

public class GuidedToursDialog {

    public interface ValueListener {
        void onValue(boolean saveDialog);
    }

    public static AlertDialog getDialog(Context context) {

        String title = context.getResources().getString(R.string.thank_you_title);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_guided_tours, null);

        TextView titleTextView = new TextView(context);
        titleTextView.setText(title);
        titleTextView.setPadding(10, 10, 10, 10);
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));
        titleTextView.setTextSize(20);

        dialogBuilder.setCustomTitle(titleTextView);

        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        return dialogBuilder.create();

    }
}
