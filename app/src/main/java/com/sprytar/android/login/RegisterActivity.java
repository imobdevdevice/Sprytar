package com.sprytar.android.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityRegisterBinding;
import com.sprytar.android.login.DaggerRegisterComponent;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.family.FamilyMemberActivity;
import com.sprytar.android.main.MainActivity;
import com.sprytar.android.presentation.BaseActivity;

import java.util.List;

import javax.inject.Inject;

public class RegisterActivity extends BaseActivity implements RegisterView {

  public static final String POSTCODE_EXTRA = "com.sprytar.android.login.PostcodeExtra";
  private static final int POSTCODE_REQUEST_CODE = 300;
  private Tracker mTracker;
  @Inject
  RegisterPresenter presenter;

  private ActivityRegisterBinding binding;

  boolean validEmail = false;
  boolean validPassword = false;
  boolean validPasswordLengh = false;

  Drawable errorIcon;

  public static void start(Context context) {
    Intent starter = new Intent(context, RegisterActivity.class);
    context.startActivity(starter);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

    createComponent().inject(this);
    presenter.attachView(this);

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Register Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    initUi();
    setTypeface();
  }

  private void setTypeface() {
    try {
      Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/custom_font.ttf");
      Typeface face = Typeface.createFromAsset(getAssets(), "fonts/museo.ttf");
      binding.loginTitle.setTypeface(face1);
      binding.surnameEditText.setTypeface(face);
      binding.postcodeEditText.setTypeface(face);
      binding.emailEditText.setTypeface(face);
      binding.passwordEditText.setTypeface(face);
      binding.passwordRepeatEditText.setTypeface(face);
      binding.infoTextView.setTypeface(face);
      binding.signUp.setTypeface(face);
      binding.alreadyRegistered.setTypeface(face);
    } catch (Exception e) {

    }
  }

  private void initAgeRangeSpinner() {
    String[] ageRangeList = getResources().getStringArray(R.array.age_range);
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_age_range_item, ageRangeList) {
      @NonNull @Override public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/museo.ttf");
        ((TextView) v).setTypeface(externalFont);

        return v;
      }
    };
    binding.ageRangeSpinner.setAdapter(dataAdapter);
  }

  private void initGenderRangeSpinner() {
    String[] getnderRange = getResources().getStringArray(R.array.gender_range);
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_age_range_item, getnderRange) {
      @NonNull @Override public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/museo.ttf");
        ((TextView) v).setTypeface(externalFont);

        return v;
      }
    };
    binding.genderRangeSpinner.setAdapter(dataAdapter);
  }

  private void initUi() {
    binding.alreadyRegistered.setText(Html.fromHtml(getString(R.string.already_registered)));
    binding.alreadyRegistered.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        finish();
      }
    });

    initAgeRangeSpinner();

    initGenderRangeSpinner();

    StringBuilder html = new StringBuilder();
    html.append(binding.infoTextView.getText());
    html.append(" <a href='https://sprytar.com/privacy/'>Learn more</a>");
    binding.infoTextView.setText(Html.fromHtml(html.toString()));
    binding.infoTextView.setClickable(true);
    binding.infoTextView.setMovementMethod(LinkMovementMethod.getInstance());

    binding.passwordRepeatEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View v, boolean hasFocus) {
      }
    });

    final Drawable checkIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_green_24dp, null);

    checkIcon.setBounds(0, 0, checkIcon.getIntrinsicWidth(), checkIcon.getIntrinsicHeight());

    errorIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_error_outline_red_24dp, null);

    errorIcon.setBounds(0, 0, checkIcon.getIntrinsicWidth(), checkIcon.getIntrinsicHeight());

    binding.postcodeEditText.setOnClickListener(v -> {
      PostcodeActivity.startForResult(this, POSTCODE_REQUEST_CODE);
    });

    binding.passwordEditText.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        String password = s.toString();
        if (password.isEmpty()) {
          binding.passwordEditText.setErrorWithoutText("", null);
          validPasswordLengh = false;
        } else {
          if (password.length() < 6) {
            binding.passwordEditText.setError(getString(R.string.minimum_password), errorIcon);
            validPasswordLengh = false;
          } else {
            binding.passwordEditText.setErrorWithoutText(null, checkIcon);
            validPasswordLengh = true;
          }
        }
      }

      @Override public void afterTextChanged(Editable s) {
      }
    });

    binding.passwordRepeatEditText.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
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

      @Override public void afterTextChanged(Editable s) {

      }
    });

    binding.emailEditText.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
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

      @Override public void afterTextChanged(Editable s) {

      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }

  public void onFindAdress(View view) {
    presenter.getAdressesForPostCode(binding.postcodeEditText.getText().toString());
  }

  public void onRegisterClick(View view) {
    sendRegistrationCredentials();
  }

  private void sendRegistrationCredentials() {
    if (!validEmail || !validPassword || !validPasswordLengh) {
      showInvalidParameters();
    } else {
      presenter.registerUser(this,binding.emailEditText.getText().toString(), binding.passwordEditText.getText().toString(),
          binding.postcodeEditText.getText().toString(), binding.ageRangeSpinner.getSelectedItem().toString(),
          binding.genderRangeSpinner.getSelectedItem().toString());
    }
  }

  @Override
  public void onRefreshClick() {
         super.onRefreshClick();
         sendRegistrationCredentials();
  }


  @Override public void showInvalidParameters() {

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

  private RegisterComponent createComponent() {
    return DaggerRegisterComponent.builder().applicationComponent(getApplicationComponent()).loginModule(new LoginModule()).build();
  }

  @Override public void showAddressesFound(List<String> addresses) {
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_age_range_item, addresses) {
      @NonNull @Override public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/museo.ttf");
        ((TextView) v).setTypeface(externalFont);

        return v;
      }
    };
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
  public void onSkipSingInClick(View view) {
    presenter.onSkipSingInClick();
  }

  @Override public void showMainActivity() {
    MainActivity.start(this);
  }

  @Override public void showFamilyMemberUi() {
    FamilyMemberActivity.start(this, true);
  }

  @Override public void showDialogMessage(String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
      }
    });

    builder.create().show();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == POSTCODE_REQUEST_CODE && resultCode == RESULT_OK) {
      binding.postcodeEditText.setText(data.getStringExtra(POSTCODE_EXTRA));
    }
  }
}
