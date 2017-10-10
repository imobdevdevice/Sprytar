package com.sprytar.android.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.sprytar.android.databinding.DialogImageRecognitionNoticeBinding;

public class ImageRecognitionNoticeDialog extends DialogFragment{

    private DialogImageRecognitionNoticeBinding binding;

    public static ImageRecognitionNoticeDialog newInstance() {
        ImageRecognitionNoticeDialog fragment = new ImageRecognitionNoticeDialog();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        binding = DialogImageRecognitionNoticeBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialogBuilder.setView(binding.getRoot());

        binding.btnOk.setOnClickListener(v -> dismiss());

        return dialogBuilder.create();
    }
}
