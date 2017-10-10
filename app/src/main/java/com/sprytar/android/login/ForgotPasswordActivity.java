package com.sprytar.android.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityForgotPasswordBinding;
import com.sprytar.android.login.DaggerForgotPasswordComponent;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.presentation.BaseActivity;

import javax.inject.Inject;

public class ForgotPasswordActivity extends BaseActivity implements ForgotPasswordView {

  @Inject
  ForgotPasswordPresenter presenter;

  private ActivityForgotPasswordBinding binding;
  private Tracker mTracker;

  public static void start(Context context) {

    Intent starter = new Intent(context, ForgotPasswordActivity.class);
    context.startActivity(starter);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);

    createComponent().inject(this);
    presenter.attachView(this);

    setTypeFace();
  }

  @Override protected void onResume() {
    super.onResume();
    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Forgot Password Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);
  }

  private void setTypeFace() {
    Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/custom_font.ttf");
    Typeface face = Typeface.createFromAsset(getAssets(), "fonts/museo.ttf");
    binding.forgotTitle.setTypeface(face1);
    binding.infoTextView.setTypeface(face);
    binding.emailEditText.setTypeface(face);
    binding.resetButton.setTypeface(face);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }

  private ForgotPasswordComponent createComponent() {
    return DaggerForgotPasswordComponent.builder().applicationComponent(getApplicationComponent()).loginModule(new LoginModule()).build();
  }

  @Override
  public void showLoadingIndicator() {
            showThrobber();
  }

  @Override
  public void hideLoadingIndicator() {
             hideThrobber();
  }

  @Override public void showError(String message) {
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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

  @Override
  public void onRefreshClick() {
    super.onRefreshClick();
    sendEmailCredentials();
  }

  public void onResetButtonClick(View view) {
    sendEmailCredentials();
  }

  private void sendEmailCredentials() {
    String email = binding.emailEditText.getText().toString();
    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      Toast.makeText(getApplicationContext(), "Please enter valid email address", Toast.LENGTH_LONG).show();
      return;
    }
    presenter.onResetButtonClick(email,this);
  }

  @Override public void showSuccessDialog(String message) {
    new AlertDialog.Builder(this, R.style.DialogTheme).setMessage(message)
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            showLoginUi();
          }
        })
        .show();
  }

  private void showLoginUi() {
    onBackPressed();
  }
}
