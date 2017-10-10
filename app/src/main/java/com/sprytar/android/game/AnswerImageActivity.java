package com.sprytar.android.game;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityAnswerImageBinding;
import com.sprytar.android.game.DaggerAnswerImageComponent;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.data.model.Answer;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.presentation.BaseActivity;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.util.Showcase;

import org.parceler.Parcels;

import java.io.IOException;

import javax.inject.Inject;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

public class AnswerImageActivity extends BaseActivity implements AnswerImageView,WaitingImageRecDialog.OnSkipQuestionCallback {
  private Tracker mTracker;
  private static final int CAMERA_REQUEST_CODE = 200;
  private static final int CLOSE_REQUEST_CODE = 300;

  private FancyShowCaseView showCaseView1 = null;
  private FancyShowCaseView showCaseView2 = null;
  private FancyShowCaseView showCaseView3 = null;
  private FancyShowCaseQueue showCaseQueue = null;

  private final BaseBindingAdapter.ItemClickListener<Answer> answerClickListener = new BaseBindingAdapter.ItemClickListener<Answer>() {
    @Override public void onClick(Answer item, int position) {
      presenter.onAnswerClick(item);
    }
  };

  public static final String QUESION_EXTRA = "com.sprytar.android.game.QuestionExtra";

  @Inject
  AnswerImagePresenter presenter;
  private ActivityAnswerImageBinding binding;

  public static void startForResult(Activity activity, Question question, int requestCode) {
    Intent starter = new Intent(activity, AnswerImageActivity.class);
    starter.putExtra(QUESION_EXTRA, Parcels.wrap(question));
    activity.startActivityForResult(starter, requestCode);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_answer_image);

    createAnswerComponent().inject(this);
    presenter.attachView(this);

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Answer Image Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());

    initUi();

    Question question = Parcels.unwrap(getIntent().getParcelableExtra(QUESION_EXTRA));
    presenter.setQuestion(question);

    initShowcase();
    showNotice();
  }

  private void showNotice(){
    ImageRecognitionNoticeDialog fragment = ImageRecognitionNoticeDialog.newInstance();

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.add(fragment, null);
    ft.commitAllowingStateLoss();
  }

  @Override
  public void showWaitingDialog() {
    WaitingImageRecDialog fragment = new WaitingImageRecDialog().newInstance();
    fragment.setOnSkipQuestionCallback(this);

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.add(fragment, null);
    ft.commitAllowingStateLoss();
  }

  private void initUi() {
  }

  @Override public void showQuestion(Question question, boolean questionAnswered, boolean questionAnsweredCorrectly) {

    binding.idTextView.setText(String.valueOf(question.getId() + 1));
    //binding.questionTextView.setText(question.getText());

    Fragment fragment = null;

    if (questionAnswered) {
      binding.skipButton.setText(getString(R.string.quiz_next_button));
      binding.skipButton.setBackgroundResource(R.drawable.rectangle_quiz_button);
      binding.skipButton.setTextColor(getResources().getColor(R.color.colorText));

      binding.hintImageView.setVisibility(View.GONE);

      if (!questionAnsweredCorrectly) {
        fragment = WrongAnswerFragment.newInstance();
      } else {
        fragment = QuestionFragment.newInstance("", false, true, null);
      }
    } else {
      //            int resourceId = getResources().getIdentifier(question.getImagePath(), "drawable",
      //                    getPackageName());
      //            Uri imagePath = Uri.parse("res:///" + String.valueOf(resourceId));

      Uri imagePath = Uri.parse(question.getImagePath());

      fragment = QuestionFragment.newInstance(question.getText(), true, true, imagePath);
    }

    getSupportFragmentManager().beginTransaction().replace(R.id.placeholder_fragment, fragment).commit();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }

  @Override public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  @Override public void nextQuestion() {

    Intent data = new Intent();
    //data.putExtra(MEETING_EXTRA, Parcels.wrap(presenter.getMeeting()));
    setResult(RESULT_OK, data);
    finish();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  private AnswerImageComponent createAnswerComponent() {
    return DaggerAnswerImageComponent.builder().sessionComponent(getSessionComponent()).build();
  }

  @Override public void showError(String message) {

  }

  public void onNavigationButtonClick(View view) {
    presenter.onNavigationButtonClick();
  }

  public void onShowHintClick(View view) {
    presenter.onShowHintClick();
  }

  public void onShowHelpClick(View view) {
    presenter.setExplainerCompleted(false);
    Showcase.resetStep();

    showCaseView1 = Showcase.prepareShowCaseFor(findViewById(R.id.subNavigation), false, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE,
        AnswerImageActivity.this, R.layout.showcase_layout);
    showCaseQueue = new FancyShowCaseQueue().add(showCaseView1);
    showCaseQueue.show();
  }

  @Override public void showHint(String hint) {
    GameHintDialog fragment = GameHintDialog.newInstance(hint);

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.add(fragment, null);
    ft.commitAllowingStateLoss();
  }

  @Override public void close() {
    onBackPressed();
  }

  public void onTakePictureClick(View view) {
    Intent intent = new Intent();
    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
    //intent.putExtra("return-data", true);
    try {
      intent.putExtra(MediaStore.EXTRA_OUTPUT, presenter.getImageUri());
      startActivityForResult(intent, CAMERA_REQUEST_CODE);
    } catch (IOException e) {
      e.printStackTrace();

      showError(this.getString(R.string.camera_file_creation_error));
      return;
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case CAMERA_REQUEST_CODE:
        if (resultCode == Activity.RESULT_OK) {
          presenter.onCameraResult();
        }
        break;
      case CLOSE_REQUEST_CODE:
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        break;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void showCorrectAnswerActivity(Uri uri, boolean correct) {
    CorrectAnswerImageActivity.startForResult(this, CLOSE_REQUEST_CODE, uri, correct);
  }

  @Override public void showLoadingIndicator() {
    showThrobber();
  }
  @Override public void hideLoadingIndicator() {
    hideThrobber();
  }

  private void initShowcase() {
    if (presenter.getExplainerStatus()) {
      return;
    }

    binding.mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      public void onGlobalLayout() {
        //Remove the listener before proceeding
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
          binding.mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
          binding.mainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }

        showCaseView1 = Showcase.prepareShowCaseFor(findViewById(R.id.subNavigation), false, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE,
            AnswerImageActivity.this, R.layout.showcase_layout);
        showCaseQueue = new FancyShowCaseQueue().add(showCaseView1);
        showCaseQueue.show();
      }
    });
  }

  private void hideShowCase() {
    if (Showcase.getStep() == 2) {
      if (Showcase.isReadyForRemove(showCaseView1)) {
        showCaseView1.hide();
      }
    } else if (Showcase.getStep() == 3) {
      if (Showcase.isReadyForRemove(showCaseView2)) {
        showCaseView2.hide();
      }
    }
  }

  private void showRelevantShowCase() {
    if (Showcase.getStep() == 1) {
      showCaseView1 = Showcase.prepareShowCaseFor(findViewById(R.id.subNavigation), false, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE,
          AnswerImageActivity.this, R.layout.showcase_layout);
      showCaseQueue.add(showCaseView1);
    } else if (Showcase.getStep() == 2) {
      showCaseView2 = Showcase.prepareShowCaseFor(binding.placeholderFragment, true, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE,
          AnswerImageActivity.this, R.layout.showcase_layout);
      showCaseQueue.add(showCaseView2);
    }
  }

  private View.OnClickListener showcaseClickListener = new View.OnClickListener() {
    @Override public void onClick(View view) {
      int id = view.getId();
      if (id == R.id.skipExplainer) {
        Showcase.increaseStep();
        hideShowCase();
        Showcase.resetStep();
        presenter.setExplainerCompleted(true);
      } else if (id == R.id.forwardArrowShowcase) {
        Showcase.increaseStep();
        hideShowCase();
        showRelevantShowCase();
      } else if (id == R.id.backArrowShowcase) {
        Showcase.increaseStep();
        hideShowCase();
        Showcase.decreaseStepBy(2);
        showRelevantShowCase();
      }
    }
  };

  @Override
  public void onSkipQuestion() {
    presenter.onNavigationButtonClick();
  }
}
