package com.sprytar.android.game.quiz;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.sprytar.android.R;
import com.sprytar.android.data.model.Location;
import com.sprytar.android.data.model.Quiz;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.databinding.ActivityQuizGameStartBinding;
import com.sprytar.android.game.GameDistanceDialog;
import com.sprytar.android.presentation.BaseActivity;
import com.sprytar.android.util.PermissionUtils;
import com.sprytar.android.util.ui.SimpleOkDialog;

import org.parceler.Parcels;

import javax.inject.Inject;

public class QuizGameStartActivity extends BaseActivity implements QuizGameStartView {

    public static final String VENUE_EXTRA = "com.sprytar.android.game.VenueExtra";
    public static final String INSIDE_EXTRA = "com.sprytar.android.game.InsideExtra";
    public static final String LOCATION_EXTRA = "com.sprytar.android.data.model.LocationExtra";
    private static String SITE_NAME="";
    private static String INTRO_TEXT="";
    private static String START_LOCATION_TEXT="";

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;

    public static void start(Context context, VenueActivity venueActivity, boolean insideBoundaries,
                             Location location, String siteName) {
        Intent starter = new Intent(context, QuizGameStartActivity.class);
        starter.putExtra(VENUE_EXTRA, Parcels.wrap(venueActivity));
        starter.putExtra(INSIDE_EXTRA, insideBoundaries);
        starter.putExtra(LOCATION_EXTRA,Parcels.wrap(location));
        SITE_NAME = siteName;
        INTRO_TEXT =venueActivity.getIntroText();
        START_LOCATION_TEXT = venueActivity.getStartLocationText();
        context.startActivity(starter);
    }

    public void onBackClick(){
        onBackPressed();
    }

    @Inject
    QuizGameStartPresenter presenter;
    private ActivityQuizGameStartBinding binding;
    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout
                .activity_quiz_game_start);

        createQuizGameStartComponent().inject(this);

        presenter.attachView(this);

        ensurePermissions();

        initUi();

        Intent intent = getIntent();
        VenueActivity venueActivity = Parcels.unwrap(intent.getParcelableExtra(VENUE_EXTRA));
        boolean insideBoundaries  = intent.getBooleanExtra(INSIDE_EXTRA, false);
        Location location = Parcels.unwrap(intent.getParcelableExtra(LOCATION_EXTRA));
        presenter.setVenueActivity(venueActivity, insideBoundaries,location,this);
    }

    private void initUi() {
        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        setTypeFace();
        showIntroMessages();
    }

    @Override
    public void onRefreshClick() {
        super.onRefreshClick();
    //    presenter.setVenueActivity(venueActivity, insideBoundaries,location,this);
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

    private void showIntroMessages(){
        binding.venueName.setText(SITE_NAME);
        binding.gameType.setText(R.string.quiz_title);
        binding.descriptionTextView.setText(INTRO_TEXT);
        binding.startPointDes.setText(START_LOCATION_TEXT);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick();
            }
        });

    }

    @Override
    public void setVenueTitle(String title) {
        //actionBar.setTitle(title);
    }

    private QuizGameStartComponent createQuizGameStartComponent(){
        return DaggerQuizGameStartComponent.builder()
                .sessionComponent(getSessionComponent())
                .build();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
    public void startQuiz(VenueActivity venueActivity, int venueId, Location location, Quiz quiz) {
       if(quiz != null){
           QuizGameActivity.start(this,venueActivity,venueId,location, quiz);
           finish();
       }
    }

    public void onStartClick(View view) {
        presenter.onStartGameClick();
    }

    @Override
    public void showDistanceDialog() {
        GameDistanceDialog dialog = GameDistanceDialog.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(dialog, null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void showDialogMessage(String message) {
        SimpleOkDialog.getDialog(this, message).show();
    }

    private void ensurePermissions() {
        if (!PermissionUtils.checkPermission(this, Manifest.permission.CAMERA)) {
            PermissionUtils.requestPermissions(this, getString(R.string
                            .permission_rationale_camera),
                    CAMERA_PERMISSION_REQUEST_CODE, Manifest.permission.CAMERA);
        } else {
            presenter.setCameraPermissionsGranted(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE: {
                if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest
                        .permission.CAMERA)) {
                    presenter.setCameraPermissionsGranted(true);
                } else {
                    presenter.setCameraPermissionsGranted(false);
                }
                break;
            }
        }
    }

}
