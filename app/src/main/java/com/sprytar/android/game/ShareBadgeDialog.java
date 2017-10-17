package com.sprytar.android.game;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sprytar.android.R;
import com.sprytar.android.data.model.EarnedBadge;
import com.sprytar.android.data.model.Location;
import com.sprytar.android.databinding.DialogShareFbImageBinding;
import com.sprytar.android.databinding.DialogShareFbPhotoBinding;
import com.sprytar.android.util.BadgeUtils;
import com.squareup.picasso.Picasso;


public class ShareBadgeDialog {

    public interface ShareBadgeDialogListener {
        void onClose();

        void onShare(String message);
    }

    public static AlertDialog getDialogWithImage(Context context, final ShareBadgeDialogListener listener, String imageUri) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_share_image, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);

        ((SimpleDraweeView) dialogView.findViewById(R.id.imageToShare)).setImageURI(Uri.decode(imageUri));

        String preparedText = "I just completed a Photo Hunt challenge on Sprytar";

        copyTextToClipboard(preparedText, context);

        Button btnClose = (Button) dialogView.findViewById(R.id.closeButton);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClose();
                }
            }
        });

        Button btnShare = (Button) dialogView.findViewById(R.id.postFacebookButton);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onShare("");
                }
            }
        });

        return dialogBuilder.create();
    }

    public static AlertDialog getDialog(Context context, final ShareBadgeDialogListener listener, EarnedBadge badge, String location) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_share_fb_image, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);

        if (badge != null) {
            ((TextView) dialogView.findViewById(R.id.badge_title)).setText(badge.getBadgeName());

            /*
            Uri uri = null;
            try {
                uri = Utils.getSvgUri(context, badge.getBadgeName(), badge.getBadgeIcon());
            } catch (Exception e) {
            }
            */

            ((ImageView) dialogView.findViewById(R.id.badge_imageView)).setImageDrawable(
                    context.getResources().getDrawable(BadgeUtils.getBadgeResource(
                            badge.getBadgeName()
                    ))
            );

            //  ((SimpleDraweeView) dialogView.findViewById(R.id.badgeIcon)).setImageURI(uri);

            //   String preparedText = getPreparedTextForShare(badge, location);
            //  ((TextView) dialogView.findViewById(R.id.badgeDesc)).setText(preparedText);

            //   copyTextToClipboard(preparedText, context);

            ImageView btnClose = (ImageView) dialogView.findViewById(R.id.close);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onClose();
                    }
                }
            });

            Button btnShare = (Button) dialogView.findViewById(R.id.postFacebookButton);
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onShare("");
                    }
                }
            });
        }

        return dialogBuilder.create();
    }

    public static AlertDialog getDialogWithLocation(Context context, final ShareBadgeDialogListener
            listener, EarnedBadge badge, Location location) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        DialogShareFbPhotoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_share_fb_photo,
                null, false);
//        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_share_fb_photo, null);

        dialogBuilder.setView(binding.getRoot());
        dialogBuilder.setCancelable(true);

        if (badge != null) {
            binding.badgeTitle.setText(badge.getBadgeName());
//            ((TextView) dialogView.findViewById(R.id.badge_title)).setText(badge.getBadgeName());

            binding.badgeImageView.setImageURI(location.getImageLink());
//            ((SimpleDraweeView) dialogView.findViewById(R.id.badge_imageView)).setImageURI(location.getImageLink());

            binding.close.setOnClickListener((View view) -> {
                if (listener != null) {
                    listener.onClose();
                }
            });

            binding.postFacebookButton.setOnClickListener((View view) -> {
                if (listener != null) {
                    listener.onShare(binding.messageEditText.getText().toString());
                }
            });
        }

        return dialogBuilder.create();
    }

    private static String getPreparedTextForShare(EarnedBadge badge, String location) {
        StringBuilder builder = new StringBuilder();
        builder.append("I've just won the ");
        builder.append(badge.getBadgeName());
        builder.append(" badge with Sprytar at ");
        builder.append(location);
        builder.append(".");

        return builder.toString();
    }

    private static void copyTextToClipboard(String text, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }

    public static AlertDialog getFacebookShareResult(Context context, ShareBadgeDialogListener listener, String imageUrl, String message) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        DialogShareFbImageBinding binding = DialogShareFbImageBinding.inflate(LayoutInflater.from(context), null, false);

        dialogBuilder.setView(binding.getRoot());
        dialogBuilder.setCancelable(true);
        binding.badgeLinear.setVisibility(View.GONE);
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClose();
                }
            }
        });
        binding.postFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onShare("");
                }
            }
        });

        Picasso.with(context)
                .load(imageUrl)
                .error(context.getResources().getDrawable(R.drawable.ic_error_outline_red_24dp))
                .into(binding.imageToShare);
        binding.messageEditText.setText(message);

        return dialogBuilder.create();
    }


}
