package com.sprytar.android.fitness;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.sprytar.android.R;
import com.sprytar.android.databinding.DialogFitnessClassesNotifyBinding;
import com.sprytar.android.fitness.DaggerFitnessClassesComponent;
import com.sprytar.android.presentation.BaseDialogFragment;

import java.util.List;

import javax.inject.Inject;


public class FitnessClassesNotifyDialog extends BaseDialogFragment implements FitnessClassesNotifyView, Validator.ValidationListener{

    private DialogFitnessClassesNotifyBinding binding;

    private Validator validator;

    @NotEmpty(message = "Missing subject")
    private EditText subjectEditText;

    @NotEmpty(message = "Missing message")
    private EditText messageEditText;

    @SuppressWarnings("WeakerAccess")
    @Inject
    FitnessClassesNotifyPresenter presenter;


    public static FitnessClassesNotifyDialog newInstance() {
        FitnessClassesNotifyDialog fragment = new FitnessClassesNotifyDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        binding = DialogFitnessClassesNotifyBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialogBuilder.setView(binding.getRoot());

        createComponent().inject(this);
        presenter.attachView(this);

        TextView title = new TextView(this.getContext());
        title.setText(getActivity().getString(R.string.let_us_know_title));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryText));
        title.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorBackground));
        title.setTextSize(20);

        dialogBuilder.setCustomTitle(title);
        validator = new Validator(this);
        validator.setValidationListener(this);

        initUi();

        return dialogBuilder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        presenter.onDestroyed();
        super.onDestroy();
    }

    private FitnessClassesComponent createComponent() {
        return DaggerFitnessClassesComponent
                .builder()
                .sessionComponent(getSessionComponent())
                .build();
    }

    private void initUi() {

        subjectEditText = binding.subjectEditText;
        messageEditText = binding.messageEditText;

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

    }

    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void closeDialog() {
        dismiss();
    }

    @Override
    public void onValidationSucceeded() {
        String subject = subjectEditText.getText().toString();
        String message = messageEditText.getText().toString();
        presenter.onSendClick(subject, message);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                showError(message);
            }
        }
    }

}