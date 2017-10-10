package com.sprytar.android.support;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityRequestSentBinding;
import com.sprytar.android.SprytarApplication;

public class RequestSentActivity extends AppCompatActivity {

  public static final String EMAIL_EXTRA = "com.sprytar.android.support.EmailExtra";

  ActivityRequestSentBinding binding;
  private Tracker mTracker;
  String email;

  public static void startForResult(Activity activity, String email, int requestCode) {
    Intent starter = new Intent(activity, RequestSentActivity.class);
    starter.putExtra(EMAIL_EXTRA, email);
    activity.startActivityForResult(starter, requestCode);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_request_sent);

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Request Sent");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    email = getIntent().getStringExtra(EMAIL_EXTRA);
    initUi();
  }

  private void initUi() {

    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getString(R.string.request_title_short));

    binding.confirmTextView.setText(getString(R.string.request_confirm_detail, email));
  }

  @Override public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }
}
