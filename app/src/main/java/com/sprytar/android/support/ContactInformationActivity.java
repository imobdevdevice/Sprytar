package com.sprytar.android.support;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityContactInformationBinding;
import com.sprytar.android.SprytarApplication;

public class ContactInformationActivity extends AppCompatActivity {

  ActivityContactInformationBinding binding;
  private Tracker mTracker;

  public static void start(Context context) {
    Intent starter = new Intent(context, ContactInformationActivity.class);
    context.startActivity(starter);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_information);

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Contact Info Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    initUi();
  }

  private void initUi() {
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getString(R.string.contact_information_title));
  }

  @Override public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }
}
