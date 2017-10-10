package com.sprytar.android.game;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityImageRecognitionMapBinding;
import com.sprytar.android.game.DaggerImageRecognitionMapComponent;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.presentation.BaseActivity;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

public class ImageRecognitionMapActivity extends BaseActivity implements ImageRecognitionMapView,QuizGroupFragment.Callback, LookZoneFragment.Callback {

    public static final String VENUE_EXTRA = "com.sprytar.android.game.VenueExtra";
    public static final String LAT_LNG_EXTRA = "com.sprytar.android.game.LatLngExtra";
    public static final String MARKERS_EXTRA = "com.sprytar.android.game.MarkersExtra";
    public static final String QUESTION_EXTRA = "com.sprytar.android.game.QuestionExtra";

    private Tracker mTracker;

    public static void startForResult(Activity activity, VenueActivity venueActivity, List<LocationBoundary> boundaries, int requestCode,
                                      LatLng currentLatLn, Question question) {

        Intent starter = new Intent(activity, ImageRecognitionMapActivity.class);
        starter.putExtra(VENUE_EXTRA, Parcels.wrap(venueActivity));
        starter.putExtra(MARKERS_EXTRA, Parcels.wrap(boundaries));
        starter.putExtra(LAT_LNG_EXTRA, Parcels.wrap(currentLatLn));
        starter.putExtra(QUESTION_EXTRA, Parcels.wrap(question));

        activity.startActivityForResult(starter, requestCode);
    }

    @Inject
    ImageRecognitionMapPresenter presenter;

    @Inject
    QuizActivity.QuestionAdapter questionAdapter;

    private ActivityImageRecognitionMapBinding binding;
    private ActionBar actionBar;
    private FragmentManager fragmentManager;
    private LookZoneFragment lookZoneFragment;
    private QuizGroupFragment quizGroupFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_recognition_map);

        createImageRecognitionMapComponent().inject(this);

        presenter.attachView(this);

        initTracker();

        fragmentManager = getSupportFragmentManager();

        initUi();

        Intent intent = getIntent();
        VenueActivity venueActivity = Parcels.unwrap(intent.getParcelableExtra(VENUE_EXTRA));
        List<LocationBoundary> boundaries = Parcels.unwrap(intent.getParcelableExtra(MARKERS_EXTRA));
        Question question = Parcels.unwrap(intent.getParcelableExtra(QUESTION_EXTRA));
        LatLng latLng = Parcels.unwrap(intent.getParcelableExtra(LAT_LNG_EXTRA));

        presenter.setVenueActivity(venueActivity, boundaries, question, latLng);
    }

    public void onShowHintClick(View view) {
        presenter.onShowHintClick();
    }

    @Override
    public void showHint(String hint) {
        GameHintDialog fragment = GameHintDialog.newInstance(hint);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fragment, null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void showVenueActivity(VenueActivity venueActivity, Question question) {
        actionBar.setTitle(venueActivity.getName());
        binding.setVenue(venueActivity);
        binding.questionTextView.setText(question.getText());

        questionAdapter.setCurrentQuestion(question);
        questionAdapter.setItems(venueActivity.getQuestions());
        binding.itemsRecyclerView.scrollToPosition(question.getId());
    }

    @Override public void showFragments(List<LocationBoundary> boundaries, LatLng currentLatLn, double latitude, double longitude) {

        quizGroupFragment = QuizGroupFragment.newInstance(boundaries, currentLatLn);
        quizGroupFragment.setCallback(this);
        if (lookZoneFragment != null) {
            fragmentManager.beginTransaction().remove(lookZoneFragment).commit();
        }

        fragmentManager.beginTransaction()
                .replace(R.id.placeholder_fragment, quizGroupFragment, "GROUP_FRAGMENT")
                .commitAllowingStateLoss();
    }

    private void initUi() {
        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.game_title));

        GridLayoutManager layoutManager = new GridLayoutManager(this, 6);
        binding.itemsRecyclerView.setLayoutManager(layoutManager);
        binding.itemsRecyclerView.setAdapter(questionAdapter);
    }


    private ImageRecognitionMapComponent createImageRecognitionMapComponent() {
        return DaggerImageRecognitionMapComponent.builder().sessionComponent(getSessionComponent()).build();
    }

    private void initTracker() {
        mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
        mTracker.setScreenName("Photo Hunt Screen");
        mTracker.send(new HitBuilders.EventBuilder().build());
        mTracker.enableAutoActivityTracking(true);
    }

    @Override public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onReadyToAnswerClick() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onNextQuestion() {

    }

    @Override
    public void onSkipQuestion() {

    }

    @Override
    public void onInSpryteZone() {
        LookZoneFragment lookZoneFragment = LookZoneFragment.newInstance(null);
        lookZoneFragment.setCallback(this);
        if (quizGroupFragment != null) {
            fragmentManager.beginTransaction().remove(quizGroupFragment).commit();
            quizGroupFragment = null;
        }

        fragmentManager.beginTransaction().replace(R.id.placeholder_fragment, lookZoneFragment).commitAllowingStateLoss();

    }
}
