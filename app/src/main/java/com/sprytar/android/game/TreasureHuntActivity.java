package com.sprytar.android.game;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityTreasureHuntBinding;
import com.sprytar.android.game.DaggerTreasureHuntComponent;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.game.treasureHuntIntro.DialogTreasureHuntIntro;
import com.sprytar.android.game.treasureHuntIntro.TreasureHuntDialogListner;
import com.sprytar.android.presentation.BaseActivity;
import com.sprytar.android.util.PermissionUtils;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

public class TreasureHuntActivity extends BaseActivity implements TreasureHuntView, TreasureHuntDialogListner, View.OnClickListener{

    public static final String VENUE_EXTRA = "com.sprytar.android.game.VenueExtra";
    public static final String MARKERS_EXTRA = "com.sprytar.android.game.MarkersExtra";
    public static final String INSIDE_EXTRA = "com.sprytar.android.game.InsideExtra";
    public static final String VENUE_ID_EXTRA = "com.sprytar.android.game.VenueIdExtra";
    public static final String LOCATION_BOUNDARIES_EXTRA = "com.sprytar.android.data.model.LocationBoundariesExtra";
    public static final String LOCATION_NAME_EXTRA = "location_name_extra";
    public static final String IMAGE_URL_EXTRA = "image_url_extra";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final int CLOSE_REQUEST_CODE = 11;

    @Inject
    TreasureHuntPresenter presenter;
    private ActivityTreasureHuntBinding binding;
    private boolean cameraPermissionsGranted;
    private boolean locationPermissionGranted;
    private static String INTRO_TEXT="";
    private static String START_LOCATION_TEXT="";
    private static String SITE_NAME="";
    private Tracker mTracker;

    public static void start(Context context, VenueActivity venueActivity, boolean insideBoundaries, int locationId, String locationName,
                             String imageUrl, List<LocationBoundary> boundaries, String siteName) {
        Intent starter = new Intent(context, TreasureHuntActivity.class);
        starter.putExtra(VENUE_EXTRA, Parcels.wrap(venueActivity));
        starter.putExtra(INSIDE_EXTRA, insideBoundaries);
        starter.putExtra(LOCATION_BOUNDARIES_EXTRA, Parcels.wrap(boundaries));
        starter.putExtra(VENUE_ID_EXTRA, locationId);
        starter.putExtra(LOCATION_NAME_EXTRA, locationName);
        starter.putExtra(IMAGE_URL_EXTRA, imageUrl);
        SITE_NAME = siteName;
        INTRO_TEXT =venueActivity.getIntroText();
        START_LOCATION_TEXT = venueActivity.getStartLocationText();

        context.startActivity(starter);
    }

    public void onBackClick(){
        onBackPressed();
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

        setVenueActivity();
    }



    @Override
    public void onRefreshClick() {
        super.onRefreshClick();
        setVenueActivity();
    }

    private void setVenueActivity() {
        Intent intent = getIntent();
        VenueActivity venueActivity = Parcels.unwrap(intent.getParcelableExtra(VENUE_EXTRA));
        boolean insideBoundaries = intent.getBooleanExtra(INSIDE_EXTRA, false);
        List<LocationBoundary> boundaries = Parcels.unwrap(intent.getParcelableExtra(LOCATION_BOUNDARIES_EXTRA));
        int locationId = intent.getIntExtra(VENUE_ID_EXTRA, -1);
        String locationName = intent.getStringExtra(LOCATION_NAME_EXTRA);
        String imageUrl = intent.getStringExtra(IMAGE_URL_EXTRA);

        presenter.setVenueActivity(venueActivity, insideBoundaries, locationId, locationName, imageUrl, boundaries,TreasureHuntActivity.this);
    }

    private void initUi() {

        setMovementMethod();
        showIntroMessages();
        setTypeFace();
        setSupportActionBar(binding.toolbar);


       // actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setTitle(getString(R.string.treasure_hunt));

    }

    private void setMovementMethod() {
        binding.descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        binding.startPointDes.setMovementMethod(new ScrollingMovementMethod());
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
       // actionBar.setTitle(venueActivity.getName());
        binding.setVenue(venueActivity);
        if(venueActivity.getQuestions() !=null)
        binding.tvTotalQuestions.setText(String.valueOf(venueActivity.getQuestions().size()));

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
            QuizActivity.startForResult(this, venueActivity, boundaries, venueId, CLOSE_REQUEST_CODE, locationName, locationImageUrl,SITE_NAME);
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

    private void showIntroMessages(){
        binding.venueName.setText(SITE_NAME);
        binding.gameType.setText(R.string.treasure_hunt);
        binding.descriptionTextView.setText(INTRO_TEXT);
        binding.startPointDes.setText(START_LOCATION_TEXT);

        binding.backArrow.setOnClickListener(this);

        binding.ivInfo.setOnClickListener(this);
    }


    private void setTypeFace() {
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/museo_sans_rounded700.otf");
        Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/exljbris _museo_sans_300.otf");
        binding.descriptionTextView.setTypeface(face);
        binding.startPointDes.setTypeface(face);
        binding.descriptionTitle.setTypeface(face);
        binding.startPointTitle.setTypeface(face);
        binding.getStartedButton.setTypeface(face);
        binding.venueName.setTypeface(face1);
        binding.gameType.setTypeface(face1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivInfo:
                startDialog();
                break;
            case R.id.back_arrow:
                onBackClick();
                break;
        }
    }
}
