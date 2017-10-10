package com.sprytar.android.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sprytar.android.R;
import com.sprytar.android.data.model.ProfileEarnedBadges;
import com.sprytar.android.util.BadgeUtils;

public class BadgeDialog {

    public interface BadgeDialogListener {
        void onClose();
    }

    public static AlertDialog getDialog(Context context, final BadgeDialogListener listener, ProfileEarnedBadges badge, int altResource) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_badge, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);

        if (badge != null) {
            ((TextView) dialogView.findViewById(R.id.badgeTitle)).setText(badge.getName());

            /*
            Uri uri = null;
            try {
                uri = Utils.getSvgUri(context, badge.getName(), badge.getIcon());
            } catch (Exception e) {
            }
            */

            ((SimpleDraweeView) dialogView.findViewById(R.id.badgeIcon)).setImageDrawable(
                    context.getResources().getDrawable(BadgeUtils.getBadgeResource(badge.getName()))
            );

            ((TextView) dialogView.findViewById(R.id.badgeDesc)).setText(badge.getDescription());
        } else {
            ((SimpleDraweeView) dialogView.findViewById(R.id.badgeIcon)).setImageDrawable(context.getResources().getDrawable(altResource));
        }

        ImageView btnClose = (ImageView) dialogView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClose();
                }
            }
        });

        return dialogBuilder.create();

    }
}
