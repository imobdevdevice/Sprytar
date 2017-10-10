package com.sprytar.android.game.trails;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityTrailsGameStartBinding;
import com.sprytar.android.game.trails.DaggerTrailsGameStartComponent;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.SubTrail;
import com.sprytar.android.data.model.Trail;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.game.GameDistanceDialog;
import com.sprytar.android.presentation.BaseActivity;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.util.PermissionUtils;
import com.sprytar.android.util.ui.SimpleDividerDecoration;
import com.sprytar.android.util.ui.SimpleOkDialog;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

public class TrailsGameStartActivity extends BaseActivity implements TrailsGameStartView {
  private Tracker mTracker;
  public static final String VENUE_EXTRA = "com.sprytar.android.game.VenueExtra";
  public static final String INSIDE_EXTRA = "com.sprytar.android.game.InsideExtra";
  public static final String MARKERS_EXTRA = "com.sprytar.android.game.MarkersExtra";
  public static final String VENUE_ID_EXTRA = "com.sprytar.android.game.VenueIdExtra";

  private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
  private VenueActivity venueActivity;
  private boolean insideBoundaries;
  private List<LocationBoundary> boundaries;
  private int venueId;

  public static void start(Context context, VenueActivity venueActivity, boolean insideBoundaries, List<LocationBoundary> boundaries, int venueId) {
    Intent starter = new Intent(context, TrailsGameStartActivity.class);
    starter.putExtra(VENUE_EXTRA, Parcels.wrap(venueActivity));
    starter.putExtra(INSIDE_EXTRA, insideBoundaries);
    starter.putExtra(MARKERS_EXTRA, Parcels.wrap(boundaries));
    starter.putExtra(VENUE_ID_EXTRA, venueId);

    context.startActivity(starter);
  }

  private final BaseBindingAdapter.ItemClickListener<SubTrail> venueclickListener = new BaseBindingAdapter.ItemClickListener<SubTrail>() {
    @Override public void onClick(SubTrail item, int position) {
      presenter.onSubTrailClick(item);
    }
  };

  @Inject
  TrailsGameStartPresenter presenter;

  @Inject
  TrailsListAdapter adapter;
  private ActivityTrailsGameStartBinding binding;
  private ActionBar actionBar;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = DataBindingUtil.setContentView(this, R.layout.activity_trails_game_start);

    createTrailsGameComponent().inject(this);

    presenter.attachView(this);

    ensurePermissions();

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Trails Start Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    initUi();
    initRecyclerView();

    Intent intent = getIntent();
    venueActivity = Parcels.unwrap(intent.getParcelableExtra(VENUE_EXTRA));
    insideBoundaries = intent.getBooleanExtra(INSIDE_EXTRA, false);
    boundaries = Parcels.unwrap(intent.getParcelableExtra(MARKERS_EXTRA));
    venueId = intent.getIntExtra(VENUE_ID_EXTRA, -1);
    presenter.setVenueActivity(venueActivity, insideBoundaries, boundaries, venueId,TrailsGameStartActivity.this);
  }

  private TrailsGameStartComponent createTrailsGameComponent() {
    return DaggerTrailsGameStartComponent.builder().sessionComponent(getSessionComponent()).build();
  }

  private void initRecyclerView() {

    adapter.setItemClickListener(venueclickListener);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

    binding.itemsRecyclerView.setLayoutManager(layoutManager);
    binding.itemsRecyclerView.addItemDecoration(new SimpleDividerDecoration(getApplicationContext()));
    binding.itemsRecyclerView.setAdapter(adapter);
  }

  @Override public void setVenueTitle(String name) {
    actionBar.setTitle(name);
  }

  @Override public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  private void initUi() {
    setSupportActionBar(binding.toolbar);
    actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(getString(R.string.trails_game_title));
  }

  @Override public void showItems(List<SubTrail> items) {
    adapter.setItems(items);
  }

  @Override public void showError(String message) {
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
  }

  @Override public void closeActivity() {
    finish();
  }

  @Override
  public void showLoadingIndicator() {
    showThrobber();
  }

  @Override
  public void hideLoadingIndicator() {
    hideThrobber();
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
    presenter.setVenueActivity(venueActivity, insideBoundaries, boundaries, venueId,TrailsGameStartActivity.this);
  }

  @Override public void startSubTrailsGame(Trail trail, SubTrail subTrail, List<LocationBoundary> boundries, int venueId) {
    TrailsGameActivity.start(getApplicationContext(), trail, subTrail, boundries, venueId);
  }

  @Override public void showDistanceDialog() {
    GameDistanceDialog dialog = GameDistanceDialog.newInstance();
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.add(dialog, null);
    ft.commitAllowingStateLoss();
  }

  private void ensurePermissions() {

    if (!PermissionUtils.checkPermission(this, Manifest.permission.CAMERA)) {
      PermissionUtils.requestPermissions(this, getString(R.string.permission_rationale_camera), CAMERA_PERMISSION_REQUEST_CODE,
          Manifest.permission.CAMERA);
    } else {
      presenter.setCameraPermissionsGranted(true);
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    switch (requestCode) {
      case CAMERA_PERMISSION_REQUEST_CODE: {
        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.CAMERA)) {
          presenter.setCameraPermissionsGranted(true);
        } else {
          presenter.setCameraPermissionsGranted(false);
        }
        break;
      }
    }
  }

  @Override public void showDialogMessage(String message) {
    SimpleOkDialog.getDialog(this, message).show();
  }


}
