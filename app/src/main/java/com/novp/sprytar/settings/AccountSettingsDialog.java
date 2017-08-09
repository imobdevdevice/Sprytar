package com.novp.sprytar.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.novp.sprytar.R;
import com.novp.sprytar.databinding.DialogAccountSettingsBinding;
import com.novp.sprytar.family.FamilyMemberActivity;
import com.novp.sprytar.presentation.BaseDialogFragment;

import javax.inject.Inject;


public class AccountSettingsDialog extends BaseDialogFragment implements AccountSettingsView {

    public static final String FAMILY_MEMBER_PARAM = "com.novp.sprytar.game.FamilyMemberParam";

    @SuppressWarnings("WeakerAccess")
    @Inject
    AccountSettingsPresenter presenter;

    private DialogAccountSettingsBinding binding;

    boolean validEmail = true;
    boolean validPassword = false;
    boolean validPasswordLengh = false;

    Drawable errorIcon;

    public static AccountSettingsDialog newInstance() {
        AccountSettingsDialog fragment = new AccountSettingsDialog();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        binding = DialogAccountSettingsBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialogBuilder.setView(binding.getRoot());

        createComponent().inject(this);
        presenter.attachView(this);

        TextView title = new TextView(this.getContext());
        title.setText(getActivity().getString(R.string.account_settings_title));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryText));
        title.setTextSize(20);

        dialogBuilder.setCustomTitle(title);

        presenter.loadAccountData();

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

    private void initUi() {

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validEmail || !validPassword || !validPasswordLengh) {
                    showInvalidParameters();
                } else {
                    presenter.onOkClick(
                            binding.emailEditText.getText().toString(),
                            binding.passwordEditText.getText().toString());
                }


            }
        });

        final Drawable checkIcon = ResourcesCompat.getDrawable(getResources(), R.drawable
                .ic_check_green_24dp, null);

        checkIcon.setBounds(0, 0, checkIcon.getIntrinsicWidth(), checkIcon.getIntrinsicHeight());

        errorIcon = ResourcesCompat.getDrawable(getResources(), R.drawable
                .ic_error_outline_red_24dp, null);

        errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());

        binding.passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                if (password.isEmpty()) {
                    binding.passwordEditText.setErrorWithoutText("", null);
                    validPasswordLengh = false;
                } else {
                    if (password.length() < 6) {
                        binding.passwordEditText.setError(getString(R.string.minimum_password),
                                errorIcon);
                        validPasswordLengh = false;
                    } else {
                        binding.passwordEditText.setErrorWithoutText(null ,checkIcon);
                        validPasswordLengh = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.passwordRepeatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = binding.passwordEditText.getText().toString();
                String confirmation = s.toString();
                if (password.isEmpty()) {
                    binding.passwordRepeatEditText.setErrorWithoutText("", null);
                    validPassword = false;
                } else {
                    if (password.equals(confirmation)) {
                        binding.passwordRepeatEditText.setErrorWithoutText("", checkIcon);
                        validPassword = true;
                    } else {
                        binding.passwordRepeatEditText.setErrorWithoutText("", null);
                        validPassword = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();
                if (email.isEmpty()) {
                    binding.emailEditText.setErrorWithoutText("", null);
                    validEmail = false;
                } else {
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        binding.emailEditText.setErrorWithoutText("", checkIcon);
                        validEmail = true;
                    } else {
                        binding.emailEditText.setErrorWithoutText("", null);
                        validEmail = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private AccountSettingsComponent createComponent() {
        return DaggerAccountSettingsComponent
                .builder()
                .sessionComponent(getSessionComponent())
                .build();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void closeDialog(boolean result) {
//        FamilyMemberActivity callingActivity = (FamilyMemberActivity) getActivity();
//        callingActivity.onAddMemberDialogResult(FamilyMemberActivity.RESULT_OK);
        dismiss();
    }

    @Override
    public void showAccountData(String email) {
        binding.emailEditText.setText(email);
    }

    @Override
    public void showInvalidParameters() {

        if (!validEmail) {
            binding.emailEditText.setErrorWithoutText("", errorIcon);
        }

        if (!validPassword) {
            binding.passwordRepeatEditText.setErrorWithoutText("", errorIcon);
        }

        if (!validPasswordLengh) {
            binding.passwordEditText.setError(getString(R.string.minimum_password), errorIcon);
        }

    }

}