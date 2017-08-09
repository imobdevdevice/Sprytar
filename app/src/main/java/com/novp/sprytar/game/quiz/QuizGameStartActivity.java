package com.novp.sprytar.game.quiz;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.novp.sprytar.R;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.Quiz;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.databinding.ActivityQuizGameStartBinding;
import com.novp.sprytar.game.GameDistanceDialog;
import com.novp.sprytar.game.GameRulesDialog;
import com.novp.sprytar.presentation.BaseActivity;
import com.novp.sprytar.util.PermissionUtils;
import com.novp.sprytar.util.ui.SimpleOkDialog;

import org.parceler.Parcels;

import javax.inject.Inject;

public class QuizGameStartActivity extends BaseActivity implements QuizGameStartView{

    public static final String VENUE_EXTRA = "com.novp.sprytar.game.VenueExtra";
    public static final String INSIDE_EXTRA = "com.novp.sprytar.game.InsideExtra";
    public static final String LOCATION_EXTRA = "com.novp.sprytar.data.model.LocationExtra";

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;

    public static void start(Context context, VenueActivity venueActivity, boolean insideBoundaries,
                             Location location) {
        Intent starter = new Intent(context, QuizGameStartActivity.class);
        starter.putExtra(VENUE_EXTRA, Parcels.wrap(venueActivity));
        starter.putExtra(INSIDE_EXTRA, insideBoundaries);
        starter.putExtra(LOCATION_EXTRA,Parcels.wrap(location));

        context.startActivity(starter);
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
        presenter.setVenueActivity(venueActivity, insideBoundaries,location);
    }

    private void initUi() {
        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.quiz_game_title));
    }

    @Override
    public void setVenueTitle(String title) {
        actionBar.setTitle(title);
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

    public void onViewRulesClick(View view) {
        presenter.onViewRulesClick();
    }

    @Override
    public void showRules() {
        GameRulesDialog.type = GameRulesDialog.TYPE_PHOTO_HINT;
        GameRulesDialog rulesDialog = GameRulesDialog.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(rulesDialog, null);
        ft.commitAllowingStateLoss();
    }



    @Override
    public void startQuiz(VenueActivity venueActivity, int venueId, Location location, Quiz quiz) {
        QuizGameActivity.start(this,venueActivity,venueId,location, quiz);
        finish();
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
