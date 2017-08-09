package com.novp.sprytar.game;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.databinding.ActivityTreasureHuntBinding;
import com.novp.sprytar.game.treasureHuntIntro.DialogTreasureHuntIntro;
import com.novp.sprytar.game.treasureHuntIntro.TreasureHuntDialogListner;
import com.novp.sprytar.presentation.BaseActivity;
import com.novp.sprytar.util.PermissionUtils;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

public class TreasureHuntActivity extends BaseActivity implements TreasureHuntView, TreasureHuntDialogListner {

    public static final String VENUE_EXTRA = "com.novp.sprytar.game.VenueExtra";
    public static final String MARKERS_EXTRA = "com.novp.sprytar.game.MarkersExtra";
    public static final String INSIDE_EXTRA = "com.novp.sprytar.game.InsideExtra";
    public static final String VENUE_ID_EXTRA = "com.novp.sprytar.game.VenueIdExtra";
    public static final String LOCATION_BOUNDARIES_EXTRA = "com.novp.sprytar.data.model.LocationBoundariesExtra";
    public static final String LOCATION_NAME_EXTRA = "location_name_extra";
    public static final String IMAGE_URL_EXTRA = "image_url_extra";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final int CLOSE_REQUEST_CODE = 11;

    @Inject
    TreasureHuntPresenter presenter;
    private ActivityTreasureHuntBinding binding;
    private ActionBar actionBar;
    private boolean cameraPermissionsGranted;
    private boolean locationPermissionGranted;
    private Tracker mTracker;

    public static void start(Context context, VenueActivity venueActivity, boolean insideBoundaries, int locationId, String locationName,
                             String imageUrl, List<LocationBoundary> boundaries) {
        Intent starter = new Intent(context, TreasureHuntActivity.class);
        starter.putExtra(VENUE_EXTRA, Parcels.wrap(venueActivity));
        starter.putExtra(INSIDE_EXTRA, insideBoundaries);
        starter.putExtra(LOCATION_BOUNDARIES_EXTRA, Parcels.wrap(boundaries));
        starter.putExtra(VENUE_ID_EXTRA, locationId);
        starter.putExtra(LOCATION_NAME_EXTRA, locationName);
        starter.putExtra(IMAGE_URL_EXTRA, imageUrl);

        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_treasure_hunt);

        createTreasureHuntComponent().inject(this);
        presenter.attachView(this);

        mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
        mTracker.setScreenName("TresureHunt Screen");
        mTracker.send(new HitBuilders.EventBuilder().build());
        mTracker.enableAutoActivityTracking(true);

        ensurePermissions();

        initUi();

        Intent intent = getIntent();
        VenueActivity venueActivity = Parcels.unwrap(intent.getParcelableExtra(VENUE_EXTRA));
        boolean insideBoundaries = intent.getBooleanExtra(INSIDE_EXTRA, false);
        List<LocationBoundary> boundaries = Parcels.unwrap(intent.getParcelableExtra(LOCATION_BOUNDARIES_EXTRA));
        int locationId = intent.getIntExtra(VENUE_ID_EXTRA, -1);
        String locationName = intent.getStringExtra(LOCATION_NAME_EXTRA);
        String imageUrl = intent.getStringExtra(IMAGE_URL_EXTRA);

        presenter.setVenueActivity(venueActivity, insideBoundaries, locationId, locationName, imageUrl, boundaries);
    }

    private void initUi() {

        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.game_title));
    }

    private void ensurePermissions() {

        if (!PermissionUtils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PermissionUtils.requestPermissions(this, getString(R.string.permission_rationale_location), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            locationPermissionGranted = true;
        }

        if (!PermissionUtils.checkPermission(this, Manifest.permission.CAMERA)) {
            PermissionUtils.requestPermissions(this, getString(R.string.permission_rationale_camera), CAMERA_PERMISSION_REQUEST_CODE,
                    Manifest.permission.CAMERA);
        } else {
            cameraPermissionsGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE: {
                if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.CAMERA)) {
                    cameraPermissionsGranted = true;
                } else {
                    cameraPermissionsGranted = false;
                }
                break;
            }
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    locationPermissionGranted = true;
                } else {
                    locationPermissionGranted = false;
                }
                break;
            }
        }
    }

    @Override
    public void showVenueActivity(VenueActivity venueActivity) {
        actionBar.setTitle(venueActivity.getName());
        binding.setVenue(venueActivity);
        //binding.pointsTextView.setText(String.valueOf(venueActivity.getGamePoints()));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private TreasureHuntComponent createTreasureHuntComponent() {
        return DaggerTreasureHuntComponent.builder().sessionComponent(getSessionComponent()).gameModule(new GameModule()).build();
    }

    @Override
    public void showError(String message) {

    }

    public void onViewRulesClick(View view) {
        presenter.onViewRulesClick();
    }

    @Override
    public void showRules() {
        GameRulesDialog.type = GameRulesDialog.TYPE_TREASURE_RULES;
        GameRulesDialog rulesDialog = GameRulesDialog.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(rulesDialog, null);
        ft.commitAllowingStateLoss();
    }


    @Override
    public void startDialog() {
        DialogTreasureHuntIntro fragment = DialogTreasureHuntIntro.newInstance(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fragment, null);
        ft.commitAllowingStateLoss();
    }

    public void onStartClick(View view) {
        presenter.showIntro();
    }

    @Override
    public void startQuiz(VenueActivity venueActivity, List<LocationBoundary> boundaries, int venueId, String locationName, String locationImageUrl) {
        //startServicePopupNotice();
        if (cameraPermissionsGranted && locationPermissionGranted) {
            QuizActivity.startForResult(this, venueActivity, boundaries, venueId, CLOSE_REQUEST_CODE, locationName, locationImageUrl);
        } else {
            new AlertDialog.Builder(this, R.style.DialogTheme).setMessage(getString(R.string.not_granted_permissions))
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }

    @Override
    public void showDistanceDialog() {
        GameDistanceDialog dialog = GameDistanceDialog.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(dialog, null);
        ft.commitAllowingStateLoss();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CLOSE_REQUEST_CODE) {
            finish();
        }
    }


    @Override
    public void onDialogDismiss() {
        presenter.onStartGameClick();
    }
}
