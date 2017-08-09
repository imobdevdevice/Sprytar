package com.novp.sprytar.game.trails;

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
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.SubTrail;
import com.novp.sprytar.data.model.Trail;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.databinding.ActivityTrailsGameStartBinding;
import com.novp.sprytar.game.GameDistanceDialog;
import com.novp.sprytar.presentation.BaseActivity;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.util.PermissionUtils;
import com.novp.sprytar.util.ui.SimpleDividerDecoration;
import com.novp.sprytar.util.ui.SimpleOkDialog;
import java.util.List;
import javax.inject.Inject;
import org.parceler.Parcels;

public class TrailsGameStartActivity extends BaseActivity implements TrailsGameStartView {
  private Tracker mTracker;
  public static final String VENUE_EXTRA = "com.novp.sprytar.game.VenueExtra";
  public static final String INSIDE_EXTRA = "com.novp.sprytar.game.InsideExtra";
  public static final String MARKERS_EXTRA = "com.novp.sprytar.game.MarkersExtra";
  public static final String VENUE_ID_EXTRA = "com.novp.sprytar.game.VenueIdExtra";

  private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;

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

  @Inject TrailsGameStartPresenter presenter;

  @Inject TrailsListAdapter adapter;
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
    VenueActivity venueActivity = Parcels.unwrap(intent.getParcelableExtra(VENUE_EXTRA));
    boolean insideBoundaries = intent.getBooleanExtra(INSIDE_EXTRA, false);
    List<LocationBoundary> boundaries = Parcels.unwrap(intent.getParcelableExtra(MARKERS_EXTRA));
    int venueId = intent.getIntExtra(VENUE_ID_EXTRA, -1);
    presenter.setVenueActivity(venueActivity, insideBoundaries, boundaries, venueId);
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
