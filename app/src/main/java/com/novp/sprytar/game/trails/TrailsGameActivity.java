package com.novp.sprytar.game.trails;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.model.EarnedBadge;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.SubTrail;
import com.novp.sprytar.data.model.Trail;
import com.novp.sprytar.databinding.ActivityTrailsGameBinding;
import com.novp.sprytar.presentation.BaseActivity;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

public class TrailsGameActivity extends BaseActivity implements TrailsGameView, TrailsGroupFragment.TrailsCallback {

    public static final String TRAIL_EXTRA = "com.novp.sprytar.game.TrailExtra";
    public static final String SUB_TRAIL_EXTRA = "com.novp.sprytar.game.SubTrailExtra";
    public static final String MARKERS_EXTRA = "com.novp.sprytar.game.MarkersExtra";
    public static final String VENUE_ID_EXTRA = "com.novp.sprytar.game.VenueIdExtra";

    @Inject
    TrailsGamePresenter presenter;

    private ActivityTrailsGameBinding binding;
    private ActionBar actionBar;
    private TrailsGroupFragment trailsGroupFragment;
    private FragmentManager fragmentManager;
    private Tracker mTracker;

    public static void start(Context context, Trail trail, SubTrail subTrail, List<LocationBoundary> boundaries, int venueId) {
        Intent starter = new Intent(context, TrailsGameActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra(TRAIL_EXTRA, trail);
        starter.putExtra(SUB_TRAIL_EXTRA, subTrail);
        starter.putExtra(MARKERS_EXTRA, Parcels.wrap(boundaries));
        starter.putExtra(VENUE_ID_EXTRA, venueId);

        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_trails_game);

        createTrailsGameComponent().inject(this);

        presenter.attachView(this);

        mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
        mTracker.setScreenName("Trails Game Screen");
        mTracker.send(new HitBuilders.EventBuilder().build());
        mTracker.enableAutoActivityTracking(true);

        fragmentManager = getSupportFragmentManager();
        initUi();

        Intent intent = getIntent();
        Trail trail = (Trail) intent.getSerializableExtra(TRAIL_EXTRA);
        SubTrail subTrail = (SubTrail) intent.getSerializableExtra(SUB_TRAIL_EXTRA);
        List<LocationBoundary> boundaries = Parcels.unwrap(intent.getParcelableExtra(MARKERS_EXTRA));
        int venueId = intent.getIntExtra(VENUE_ID_EXTRA, -1);

        presenter.setTrailData(trail, subTrail, boundaries, venueId);
    }

    private TrailsGameComponent createTrailsGameComponent() {
        return DaggerTrailsGameComponent.builder().sessionComponent(getSessionComponent()).build();
    }

    @Override
    public void setTrailTitle(String title) {
        actionBar.setTitle(title);
    }

    @Override
    public void setSubTrailData(String subTitle, String info, int resource) {
        binding.title.setText(subTitle);
        binding.info.setText(info);
        binding.wheelchairSupport.setImageDrawable(getResources().getDrawable(resource));
    }

    private void initUi() {
        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.trails_game_title));
    }

    @Override
    public void setFragment(List<LocationBoundary> boundaries, SubTrail subTrail) {
        trailsGroupFragment = TrailsGroupFragment.newInstance(boundaries, subTrail);
        trailsGroupFragment.setTrailsCallback(this);
        fragmentManager.beginTransaction().replace(R.id.placeholder, trailsGroupFragment, "GROUP_FRAGMENT").commitAllowingStateLoss();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onClearMapClick(View view) {
        trailsGroupFragment.clearMap();
    }

    @Override
    public void onGameCompleted() {
        presenter.onGameCompleted();
    }

    @Override
    public void finishTheGame(Trail trail, SubTrail subTrail, EarnedBadge earnedBadge) {
        TrailsGameFinishedActivity.start(getApplicationContext(), trail, subTrail, earnedBadge);
        finish();
    }

    @Override
    public void onHideClearMapButton() {
        binding.clearMap.setVisibility(View.GONE);
    }

    @Override
    public void onShowClearMapButton() {
        binding.clearMap.setVisibility(View.VISIBLE);
    }
}
