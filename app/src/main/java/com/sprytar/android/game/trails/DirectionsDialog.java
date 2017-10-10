package com.sprytar.android.game.trails;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.sprytar.android.databinding.DialogDirectionsBinding;


public class DirectionsDialog extends DialogFragment implements View.OnClickListener {

    public interface DirectionsDialogCallback {
        void onShowMessageClick();
    }

    private static final String DIRECTIONS_EXTRA = "directions";
    private DialogDirectionsBinding binding;
    private DirectionsDialogCallback directionsDialogCallback;

    public static DirectionsDialog newInstance(String message) {
        DirectionsDialog fragment = new DirectionsDialog();

        Bundle args = new Bundle();
        args.putString(DIRECTIONS_EXTRA, message);
        fragment.setArguments(args);

        return fragment;
    }

    public void setDirectionsCallback(DirectionsDialogCallback directionsDialogCallback) {
        this.directionsDialogCallback = directionsDialogCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(DIRECTIONS_EXTRA);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        binding = DialogDirectionsBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialogBuilder.setView(binding.getRoot());

        if (message != null) {
            binding.messageTextView.setText(message);
        } else {
            binding.messageTextView.setVisibility(View.GONE);
        }

        binding.btnClose.setOnClickListener(this);
        binding.btnShowMessage.setOnClickListener(this);

        return dialogBuilder.create();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == binding.btnClose.getId()) {
            dismiss();
        } else if (id == binding.btnShowMessage.getId()) {
            dismiss();
            if (directionsDialogCallback != null) {
                directionsDialogCallback.onShowMessageClick();
            }
        }
    }
}
