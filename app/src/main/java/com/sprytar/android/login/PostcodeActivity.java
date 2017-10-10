package com.sprytar.android.login;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityPostcodeBinding;
import com.sprytar.android.login.DaggerPostcodeComponent;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.presentation.BaseActivity;

import javax.inject.Inject;

public class PostcodeActivity extends BaseActivity implements PostcodeView {

  @Inject
  PostcodePresenter presenter;

  private ActivityPostcodeBinding binding;
  private Tracker mTracker;

  public static void startForResult(Activity activity, int requestCode) {
    Intent starter = new Intent(activity, PostcodeActivity.class);
    activity.startActivityForResult(starter, requestCode);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_postcode);

    createComponent().inject(this);
    presenter.attachView(this);

    setTypeFace();

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Postcode Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    initUi();
  }

  private void initUi() {

    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getString(R.string.select_postcode_title));

    Typeface face = Typeface.createFromAsset(getAssets(), "fonts/museo.ttf");

    String[] alphabet = "abcdefghijklmnopqrstuvwxyz".toUpperCase().split("");
    String[] alphanumeric = "abcdefghijklmnopqrstuvwxyz1234567890".toUpperCase().split("");

    binding.char1Picker.setMinValue(0);
    binding.char1Picker.setMaxValue(alphabet.length - 1);
    binding.char1Picker.setDisplayedValues(alphabet);
    binding.char1Picker.setTypeface(face);

    binding.char2Picker.setMinValue(0);
    binding.char2Picker.setMaxValue(alphanumeric.length - 1);
    binding.char2Picker.setDisplayedValues(alphanumeric);
    binding.char2Picker.setTypeface(face);

    binding.char3Picker.setMinValue(0);
    binding.char3Picker.setMaxValue(alphanumeric.length - 1);
    binding.char3Picker.setDisplayedValues(alphanumeric);
    binding.char3Picker.setTypeface(face);

    binding.char4Picker.setMinValue(0);
    binding.char4Picker.setMaxValue(alphanumeric.length - 1);
    binding.char4Picker.setDisplayedValues(alphanumeric);
    binding.char4Picker.setTypeface(face);

    binding.doneButton.setOnClickListener(v -> {
      StringBuilder result = new StringBuilder();
      result.append(alphabet[binding.char1Picker.getValue()]);
      result.append(alphanumeric[binding.char2Picker.getValue()]);
      result.append(alphanumeric[binding.char3Picker.getValue()]);
      result.append(alphanumeric[binding.char4Picker.getValue()]);

      Intent intent = new Intent();
      intent.putExtra(RegisterActivity.POSTCODE_EXTRA, result.toString());
      setResult(RESULT_OK, intent);
      finish();
    });
  }

  @Override public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  private void setTypeFace() {
    Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/custom_font.ttf");
    Typeface face = Typeface.createFromAsset(getAssets(), "fonts/museo.ttf");
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }

  private PostcodeComponent createComponent() {
    return DaggerPostcodeComponent.builder().applicationComponent(getApplicationComponent()).build();
  }

  @Override public void showError(String message) {
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
  }
}
