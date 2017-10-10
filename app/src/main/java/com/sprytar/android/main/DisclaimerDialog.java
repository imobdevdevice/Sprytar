package com.sprytar.android.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sprytar.android.R;
import com.sprytar.android.databinding.DialogDisclaimerBinding;
import com.sprytar.android.presentation.BaseDialogFragment;


public class DisclaimerDialog extends BaseDialogFragment {

    private DialogDisclaimerBinding binding;

    public static DisclaimerDialog newInstance() {
        DisclaimerDialog fragment = new DisclaimerDialog();
        Bundle args = new Bundle();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        binding = DialogDisclaimerBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialogBuilder.setView(binding.getRoot());

        TextView title = new TextView(this.getContext());
        title.setText(getActivity().getString(R.string.risks_title));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryText));
        title.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorBackground));
        title.setTextSize(20);

        dialogBuilder.setCustomTitle(title);

        initUi();

        return dialogBuilder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initUi() {

        binding.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        StringBuilder html = new StringBuilder();
        html.append("<a href='launch.TcActivity://'>Terms and Conditions</a>");
        binding.tcTextView.setText(Html.fromHtml(html.toString()));
        binding.tcTextView.setClickable(true);
        binding.tcTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }


}