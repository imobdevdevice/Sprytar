package com.novp.sprytar.game.photohunt;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.databinding.ActivityPhotoHuntBinding;
import com.novp.sprytar.game.GameHintDialog;
import com.novp.sprytar.game.GameMapFragment;
import com.novp.sprytar.presentation.BaseActivity;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import org.parceler.Parcels;

public class PhotoHuntActivity extends BaseActivity implements PhotoHuntView {

  public static final String VENUE_EXTRA = "com.novp.sprytar.game.VenueExtra";
  public static final String LAT_LNG_EXTRA = "com.novp.sprytar.game.LatLngExtra";
  public static final String VENUE_ID_EXTRA = "com.novp.sprytar.game.VenueIdExtra";
  public static final String MARKERS_EXTRA = "com.novp.sprytar.game.MarkersExtra";
  public static final String QUESTION_EXTRA = "com.novp.sprytar.game.QuestionExtra";

  private static final int CAMERA_REQUEST_CODE = 200;
  private static final int CLOSE_REQUEST_CODE = 300;
  private Tracker mTracker;

  public static void startForResult(Activity activity, VenueActivity venueActivity, List<LocationBoundary> boundaries, int venueId, int requestCode,
      LatLng currentLatLn, Question question) {

    Intent starter = new Intent(activity, PhotoHuntActivity.class);
    starter.putExtra(VENUE_EXTRA, Parcels.wrap(venueActivity));
    starter.putExtra(MARKERS_EXTRA, Parcels.wrap(boundaries));
    starter.putExtra(VENUE_ID_EXTRA, venueId);
    starter.putExtra(LAT_LNG_EXTRA, Parcels.wrap(currentLatLn));
    starter.putExtra(QUESTION_EXTRA, Parcels.wrap(question));

    activity.startActivityForResult(starter, requestCode);
  }

  @Inject PhotoHuntPresenter presenter;

  private ActivityPhotoHuntBinding binding;

  private GameMapFragment gameMapFragment;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = DataBindingUtil.setContentView(this, R.layout.activity_photo_hunt);

    createPhotoHuntComponent().inject(this);

    presenter.attachView(this);

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Photo Hunt Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);
    Intent intent = getIntent();
    VenueActivity venueActivity = Parcels.unwrap(intent.getParcelableExtra(VENUE_EXTRA));
    List<LocationBoundary> boundaries = Parcels.unwrap(intent.getParcelableExtra(MARKERS_EXTRA));
    int venueId = intent.getIntExtra(VENUE_ID_EXTRA, 0);
    Question question = Parcels.unwrap(intent.getParcelableExtra(QUESTION_EXTRA));
    LatLng latLng = Parcels.unwrap(intent.getParcelableExtra(LAT_LNG_EXTRA));

    presenter.setVenueActivity(venueActivity, venueId, question);

    FragmentManager fragmentManager = getSupportFragmentManager();

    gameMapFragment = GameMapFragment.newInstance(boundaries, latLng);

    fragmentManager.beginTransaction().replace(R.id.map, gameMapFragment, "MAP").addToBackStack(null).commitAllowingStateLoss();
  }

  @Override public void checkPhotoLocation() {
    presenter.checkAnswer(gameMapFragment.getCurrentLocation());
  }

  private PhotoHuntComponent createPhotoHuntComponent() {
    return DaggerPhotoHuntComponent.builder().sessionComponent(getSessionComponent()).build();
  }

  @Override public void setQuestion(Question question) {
    Log.v("test_tag", "question url " + question.getPhotoHuntUrl());
    binding.image.setImageURI(Uri.decode(question.getPhotoHuntUrl()));
  }

  @Override public void showLoadingIndicator() {
    binding.loadingImageView.setVisibility(View.VISIBLE);
    Uri loadingGif = Uri.parse("res:///" + R.drawable.spryer_preloader256);

    SimpleDraweeView draweeView = binding.loadingImageView;

    GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());

    GenericDraweeHierarchy hierarchy = builder.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE).build();

    DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(loadingGif).setAutoPlayAnimations(true).build();

    draweeView.setHierarchy(hierarchy);
    draweeView.setController(controller);
  }

  @Override public void hideLoadingIndicator() {
    binding.loadingImageView.setVisibility(View.GONE);
  }

  public void onSkipQuestion(View view) {
    finishQuestion();
  }

  public void onTakePictureClick(View view) {
    Intent intent = new Intent();
    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
    try {
      intent.putExtra(MediaStore.EXTRA_OUTPUT, presenter.getImageUri());
      startActivityForResult(intent, CAMERA_REQUEST_CODE);
    } catch (IOException e) {
      e.printStackTrace();

      showError(this.getString(R.string.camera_file_creation_error));
      return;
    }
  }

  @Override public void onBackPressed() {
    //don't allow the user to go back at this point
  }

  @Override public void showResultActivity(String imageUri, boolean isAnswerCorrect, Question question) {
    PhotoHuntResultActivity.startForResult(this, isAnswerCorrect, imageUri, CLOSE_REQUEST_CODE, question);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case CAMERA_REQUEST_CODE:
        if (resultCode == Activity.RESULT_OK) {
          presenter.onCameraResult();
        }
        break;

      case CLOSE_REQUEST_CODE:
        finishQuestion();
        break;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void finishQuestion() {
    Intent intent = new Intent();
    setResult(RESULT_OK, intent);
    finish();
  }

  @Override public void showError(String message) {
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
  }

  public void onShowHint(View view) {
    presenter.showHint();
  }

  @Override public void showHint(String hint) {
    GameHintDialog fragment = GameHintDialog.newInstance(hint);

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.add(fragment, null);
    ft.commitAllowingStateLoss();
  }
}
