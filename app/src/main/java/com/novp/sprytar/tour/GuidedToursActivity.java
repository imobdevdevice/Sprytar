package com.novp.sprytar.tour;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.databinding.ActivityGuidedToursBinding;
import com.novp.sprytar.presentation.BaseActivity;
import java.util.List;
import javax.inject.Inject;
import org.parceler.Parcels;

public class GuidedToursActivity extends BaseActivity implements GuidedToursView {

  public static final String VENUE_EXTRA = "com.novp.sprytar.game.VenueExtra";
  public static final String MARKERS_EXTRA = "com.novp.sprytar.game.MarkersExtra";
  private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
  private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
  private Tracker mTracker;
  @Inject GuidedToursPresenter presenter;
  private ActivityGuidedToursBinding binding;
  private ActionBar actionBar;

  public static void start(Context context, VenueActivity venueActivity, List<LocationBoundary> boundaries) {
    Intent starter = new Intent(context, GuidedToursActivity.class);
    starter.putExtra(VENUE_EXTRA, Parcels.wrap(venueActivity));
    starter.putExtra(MARKERS_EXTRA, Parcels.wrap(boundaries));
    context.startActivity(starter);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_guided_tours);

    createComponent().inject(this);
    presenter.attachView(this);

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Guided Tours");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    initUi();

    VenueActivity venueActivity = Parcels.unwrap(getIntent().getParcelableExtra(VENUE_EXTRA));
    List<LocationBoundary> boundaries = Parcels.unwrap(getIntent().getParcelableExtra(MARKERS_EXTRA));
    presenter.setVenueActivity(venueActivity, boundaries);
  }

  private void initUi() {

    setSupportActionBar(binding.toolbar);
    actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(getString(R.string.tour_title));
  }

  @Override public void showVenueActivity(VenueActivity venueActivity) {
    actionBar.setTitle(venueActivity.getName());
    binding.setVenue(venueActivity);
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
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  private GuidedToursComponent createComponent() {
    return DaggerGuidedToursComponent.builder().sessionComponent(getSessionComponent()).build();
  }

  @Override public void showError(String message) {

  }

  public void onStartClick(View view) {
    presenter.onSendNotificationClick();
  }

  @Override public void showSendSuccessDialog() {
    AlertDialog dialog = GuidedToursDialog.getDialog(this);
    dialog.show();
  }

  @Override public void showDialogMessage(String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
      }
    });

    builder.create().show();
  }
}
