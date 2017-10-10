package com.sprytar.android.network;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.sprytar.android.R;
import com.sprytar.android.databinding.FragmentConnectionProblemBinding;
import com.sprytar.android.presentation.BaseDialogFragment;

/**
 * Created by imobdev-darshan on 29/8/17.
 */

public class NetworkProblemDialog extends BaseDialogFragment {


    private FragmentConnectionProblemBinding binding;
    private  String dialogTitle;
    private  String dialogMessage = "";
    private  String dialogDescr = "";
    private com.sprytar.android.network.refreshListener refreshListener;

    public void setRefreshListener(com.sprytar.android.network.refreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public NetworkProblemDialog(String dialogTitle, String dialogMessage, String dialogDescr) {
        this.dialogTitle = dialogTitle;
        this.dialogMessage = dialogMessage;
        this.dialogDescr = dialogDescr;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bg_network_dialog));
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        binding = FragmentConnectionProblemBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialogBuilder.setView(binding.getRoot());
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
        Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/custom_font.ttf");
        binding.tvDialogTitle.setTypeface(face1);
        binding.tvClose.setTypeface(face1);
        binding.tvTryAgain.setTypeface(face1);
        binding.tvDialogTitle.setText(dialogTitle);
        binding.tvDialogMessage.setText(dialogMessage);
        binding.tvDescription.setText(dialogDescr);

        binding.tvTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                refreshListener.onRefreshClick();
            }
        });

        binding.tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshListener.onCloseClick();
            }
        });

    }
}
