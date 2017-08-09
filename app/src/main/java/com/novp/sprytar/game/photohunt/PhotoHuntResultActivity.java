package com.novp.sprytar.game.photohunt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.databinding.ActivityPhotoHuntResultBinding;
import com.novp.sprytar.game.ShareBadgeDialog;
import com.novp.sprytar.presentation.BaseActivity;
import javax.inject.Inject;
import org.parceler.Parcels;

public class PhotoHuntResultActivity extends BaseActivity implements PhotoHuntResultView {
  private Tracker mTracker;
  public static final String CORRECT_ANSWER = "com.novp.sprytar.game.CorrectAnswerExtra";
  public static final String IMAGE_URI_EXTRA = "com.novp.sprytar.game.ImageUriExtra";
  public static final String QUESTION_EXTRA = "com.novp.sprytar.game.QuestionExtra";

  public static void startForResult(Activity activity, boolean isCorrectAnswer, String imageUri, int requestCode, Question question) {
    Intent starter = new Intent(activity, PhotoHuntResultActivity.class);
    starter.putExtra(CORRECT_ANSWER, isCorrectAnswer);
    starter.putExtra(IMAGE_URI_EXTRA, imageUri);
    starter.putExtra(QUESTION_EXTRA, Parcels.wrap(question));

    activity.startActivityForResult(starter, requestCode);
  }

  private ShareBadgeDialog.ShareBadgeDialogListener shareBadgeDialoglistener = new ShareBadgeDialog.ShareBadgeDialogListener() {
    @Override public void onClose() {
      shareBadgeDialog.dismiss();
    }

    @Override public void onShare(String message) {
      shareBadgeDialog.dismiss();
      shareToFacebook();
    }
  };

  private ActivityPhotoHuntResultBinding binding;
  private AlertDialog shareBadgeDialog = null;
  private Animation bounce;

  @Inject PhotoHuntResultPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = DataBindingUtil.setContentView(this, R.layout.activity_photo_hunt_result);

    createPhotoHuntResultComponent().inject(this);

    presenter.attachView(this);

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Photo Hunt Result Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);
    Intent intent = getIntent();
    boolean isCorrectAnswer = intent.getBooleanExtra(CORRECT_ANSWER, false);
    String imageUri = intent.getStringExtra(IMAGE_URI_EXTRA);
    Question question = Parcels.unwrap(getIntent().getParcelableExtra(QUESTION_EXTRA));

    initAnim();

    presenter.setData(isCorrectAnswer, imageUri, question);
  }

  private PhotoHuntResultComponent createPhotoHuntResultComponent() {
    return DaggerPhotoHuntResultComponent.builder().sessionComponent(getSessionComponent()).build();
  }

  private void initAnim() {
    bounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
  }

  public void onNextQuestion(View view) {
    presenter.nextQuestion();
  }

  @Override public void nextQuestion() {
    Intent data = new Intent();
    setResult(RESULT_OK);
    finish();
  }

  public void onShareToFB(View view) {
    shareBadgeDialog = ShareBadgeDialog.getDialogWithImage(this, shareBadgeDialoglistener, presenter.getQuestionUrl());
    shareBadgeDialog.show();
  }

  @Override public void setImage(String imageUri) {
    binding.image.setImageURI(Uri.decode(imageUri));
  }

  @Override public void setCorrectAnswer(boolean isCorrect) {
    if (isCorrect) {
      binding.imageAnswer.setImageDrawable(getResources().getDrawable(R.drawable.correct_answer_character));
    } else {
      binding.imageAnswer.setImageDrawable(getResources().getDrawable(R.drawable.wrong_answer_character));
    }

    binding.imageAnswer.setVisibility(View.VISIBLE);
    binding.imageAnswer.startAnimation(bounce);
  }

  @Override public void onBackPressed() {
    //don't allow the user to go back at this point
  }

  private void shareToFacebook() {

    try {
      Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
      if (intent != null) {
        // The application exists
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setPackage("com.facebook.katana");
        shareIntent.setType("text/plain");
        //     shareIntent.putExtra(Intent.EXTRA_TEXT, presenter.getQuestionUrl());
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.sprytar.com/");
        startActivity(shareIntent);
      }
    } catch (Exception e) {
      Log.v("test_tag", e.getMessage());
    }
  }
}
