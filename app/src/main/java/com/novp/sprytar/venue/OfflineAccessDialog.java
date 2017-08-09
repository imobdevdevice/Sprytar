package com.novp.sprytar.venue;

import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.novp.sprytar.R;

public class OfflineAccessDialog {

    public interface ValueListener {
        void onValue(boolean saveDialog);
    }

    public static AlertDialog getDialog(Context context, final boolean saveDialog, final
    ValueListener listener) {

        String title = context.getResources().getString(R.string.offline_access_title);
        String message = "";
        if (saveDialog) {
            message = context.getResources().getString(R.string.offline_access_message_save);
        } else {
            message = context.getResources().getString(R.string.offline_access_message_remove);
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_offline_access, null);
        dialogBuilder.setView(dialogView);

        final TextView textView = (TextView) dialogView.findViewById(R.id.text_textView);
        textView.setText(message);

        dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                listener.onValue(saveDialog);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        return dialogBuilder.create();

    }
}
