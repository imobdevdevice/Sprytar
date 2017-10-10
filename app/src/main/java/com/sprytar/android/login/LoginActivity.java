package com.sprytar.android.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityLoginBinding;
import com.sprytar.android.login.DaggerLoginComponent;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.main.MainActivity;
import com.sprytar.android.presentation.BaseActivity;

import javax.inject.Inject;

public class LoginActivity extends BaseActivity implements LoginView {

    @Inject
    LoginPresenter presenter;
    //535048280054-3mkmj1uio5888palfcsotbj61t7nm9gu.apps.googleusercontent.com
    private ActivityLoginBinding binding;
    Tracker mTracker;
    Drawable checkIcon;
    Drawable errorIcon;

    boolean validEmail = false;
    boolean validPassword = false;

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        createComponent().inject(this);
        presenter.attachView(this);

        mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
        mTracker.setScreenName("Login Screen");
        mTracker.send(new HitBuilders.EventBuilder().build());

        initUi();
        setTypeface();
    }

    public void setTypeface() {
        try {
            Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/custom_font.ttf");
            Typeface face = Typeface.createFromAsset(getAssets(), "fonts/museo.ttf");
            binding.signinTitle.setTypeface(face1);
            binding.emailEditText.setTypeface(face);
            binding.passwordEditText.setTypeface(face);
            binding.login.setTypeface(face);
            binding.forgotTextView.setTypeface(face);
            binding.skipTextView.setTypeface(face);
            binding.haveAccountTextView.setTypeface(face);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUi() {
        checkIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_green_24dp, null);

        checkIcon.setBounds(0, 0, checkIcon.getIntrinsicWidth(), checkIcon.getIntrinsicHeight());

        errorIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_error_outline_red_24dp, null);

        errorIcon.setBounds(0, 0, checkIcon.getIntrinsicWidth(), checkIcon.getIntrinsicHeight());

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

        binding.passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                if (password.isEmpty()) {
                    binding.passwordEditText.setErrorWithoutText("", null);
                    validPassword = false;
                } else {
                    binding.passwordEditText.setErrorWithoutText(null, checkIcon);
                    validPassword = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onLoginClick(View view) {
        onNextItemClick();
    }

    private void onNextItemClick() {
        if (!validEmail || !validPassword) {
            showInvalidParameters();
        } else {
            String email = binding.emailEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
            presenter.onLoginClick(email, password,this);
        }
    }

    private LoginComponent createComponent() {
        return DaggerLoginComponent.builder().applicationComponent(getApplicationComponent()).loginModule(new LoginModule()).build();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefreshClick() {
        super.onRefreshClick();
        onNextItemClick();
    }

    @Override
    public void showErrorDialog(boolean hasNoInternet) {
        if(hasNoInternet){
            showErrorDialog(getResources().getString(R.string.alert_message_title),getResources().getString(R.string.connection_problem_message),
                    getResources().getString(R.string.connection_problem_description));
        }else{
            showErrorDialog(getResources().getString(R.string.other_error_title),getResources().getString(R.string.other_error_message),
                    getResources().getString(R.string.other_error_descr));
        }

    }

    public void onSkipSingInClick(View view) {
        presenter.onSkipSingInClick();
    }

    public void onSignUpClick(View view) {
        RegisterActivity.start(this);
    }

    public void  onForgotPasswordClick(View view) {
        ForgotPasswordActivity.start(this);
    }

    @Override
    public void showPickUserUi() {
        PickUserActivity.start(this);
    }

    @Override
    public void showMainActivity() {
        MainActivity.start(this);
    }

    @Override
    public void showInvalidParameters() {
        if (!validEmail) {
            binding.emailEditText.setError(getString(R.string.invalid_email), errorIcon);
        }
        if (!validPassword) {
            binding.passwordEditText.setError(getString(R.string.invalid_password), errorIcon);
        }
    }

    @Override
    public void showLoadingIndicator() {
       showThrobber();
    }

    @Override
    public void hideLoadingIndicator() {
        hideThrobber();
    }
}
