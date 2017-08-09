package com.novp.sprytar.support;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.databinding.ActivitySupportRequestBinding;
import com.novp.sprytar.presentation.BaseActivity;
import javax.inject.Inject;

public class SupportRequestActivity extends BaseActivity implements SupportRequestView {

  private static final int SENT_REQUEST_CODE = 11;
  private Tracker mTracker;
  @Inject SupportRequestPresenter presenter;

  private ActivitySupportRequestBinding binding;

  public static void start(Context context) {
    Intent starter = new Intent(context, SupportRequestActivity.class);
    context.startActivity(starter);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_support_request);
    createSupportRequestComponent().inject(this);
    presenter.attachView(this);
    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Support Request");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    initUi();
  }

  private void initUi() {

    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getString(R.string.request_title_short));
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }

  @Override public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_send, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_send) {
      presenter.sendRequest(binding.requestEditText.getText().toString());
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void showResultUi(String email) {
    RequestSentActivity.startForResult(this, email, SENT_REQUEST_CODE);
  }

  private SupportRequestComponent createSupportRequestComponent() {
    return DaggerSupportRequestComponent.builder().sessionComponent(getSessionComponent()).build();
  }

  @Override public void showError(String message) {
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == SENT_REQUEST_CODE) {
      finish();
    }
  }
}
