package com.novp.sprytar.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.novp.sprytar.databinding.DialogWaitingImageRecBinding;

public class WaitingImageRecDialog extends DialogFragment {

    public interface OnSkipQuestionCallback{
        void onSkipQuestion();
    }

    private DialogWaitingImageRecBinding binding;
    private OnSkipQuestionCallback callback;

    public static WaitingImageRecDialog newInstance() {
        WaitingImageRecDialog fragment = new WaitingImageRecDialog();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        binding = DialogWaitingImageRecBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialogBuilder.setView(binding.getRoot());

        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(callback != null){
                    callback.onSkipQuestion();
                }
            }
        });

        return dialogBuilder.create();
    }

    public void setOnSkipQuestionCallback(OnSkipQuestionCallback callback){
        this.callback = callback;
    }
}
