package com.sprytar.android.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sprytar.android.R;
import com.sprytar.android.databinding.DialogDistanceBinding;

public class GameDistanceDialog extends DialogFragment {

    private DialogDistanceBinding binding;

    public static GameDistanceDialog newInstance() {
        GameDistanceDialog fragment = new GameDistanceDialog();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        binding = DialogDistanceBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialogBuilder.setView(binding.getRoot());

        TextView title = new TextView(this.getContext());
        title.setText(getActivity().getString(R.string.not_in_park));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorBackground));
        title.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryText));
        title.setTextSize(20);

        dialogBuilder.setCustomTitle(title);

        binding.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialogBuilder.create();
    }
}
