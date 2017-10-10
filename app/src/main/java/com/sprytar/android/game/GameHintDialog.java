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
import com.sprytar.android.databinding.DialogHintBinding;


public class GameHintDialog extends DialogFragment {

    public static final String HINT_PARAM = "com.sprytar.android.game.hintParam";

    private DialogHintBinding binding;

    public static GameHintDialog newInstance(String hint) {
        GameHintDialog fragment = new GameHintDialog();
        Bundle args = new Bundle();
        args.putString(HINT_PARAM, hint);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        binding = DialogHintBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialogBuilder.setView(binding.getRoot());

        TextView title = new TextView(this.getContext());
        title.setText(getActivity().getString(R.string.hint));
        title.setPadding(16, 16, 16, 16);
        title.setGravity(Gravity.LEFT);
        title.setTextColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryText));
        title.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorBackground));
        title.setTextSize(20);

        dialogBuilder.setCustomTitle(title);

        String hint = getArguments().getString(HINT_PARAM);
        binding.textTextView.setText(hint);

        binding.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialogBuilder.create();
    }
}
