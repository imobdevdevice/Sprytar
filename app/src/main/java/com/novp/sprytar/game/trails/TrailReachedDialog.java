package com.novp.sprytar.game.trails;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.novp.sprytar.databinding.DialogTrailReachedBinding;

public class TrailReachedDialog extends DialogFragment implements View.OnClickListener {

    public interface TrailReachedCallback {
        void onShowDirectionsClick();
    }

    private static final String MESSAGE_EXTRA = "message";

    private DialogTrailReachedBinding binding;
    private TrailReachedCallback trailReachedCallback;

    public static TrailReachedDialog newInstance(String message) {
        TrailReachedDialog fragment = new TrailReachedDialog();

        Bundle args = new Bundle();
        args.putString(MESSAGE_EXTRA, message);
        fragment.setArguments(args);

        return fragment;
    }

    public void setTrailReachedCallback(TrailReachedCallback trailReachedCallback) {
        this.trailReachedCallback = trailReachedCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(MESSAGE_EXTRA);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        binding = DialogTrailReachedBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialogBuilder.setView(binding.getRoot());

        if (message != null) {
            binding.messageTextView.setText(message);
        } else {
            binding.messageTextView.setVisibility(View.GONE);
        }

        binding.btnClose.setOnClickListener(this);
        binding.btnShowDirections.setOnClickListener(this);

        return dialogBuilder.create();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == binding.btnClose.getId()) {
            dismiss();
        } else if (id == binding.btnShowDirections.getId()) {
            dismiss();
            if (trailReachedCallback != null) {
                trailReachedCallback.onShowDirectionsClick();
            }
        }
    }
}
