package com.novp.sprytar.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.novp.sprytar.R;
import com.novp.sprytar.databinding.DialogHintBinding;


public class GameHintDialog extends DialogFragment {

    public static final String HINT_PARAM = "com.novp.sprytar.game.hintParam";

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
        title.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryText));
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
