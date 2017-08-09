package com.novp.sprytar.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.model.Answer;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.databinding.ActivityAnswerBinding;
import com.novp.sprytar.databinding.ItemAnswerBinding;
import com.novp.sprytar.presentation.BaseActivity;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseBindingViewHolder;
import com.novp.sprytar.util.ui.SimpleDividerDecoration;
import javax.inject.Inject;
import org.parceler.Parcels;

public class AnswerActivity extends BaseActivity implements AnswerView {

  private final BaseBindingAdapter.ItemClickListener<Answer> answerClickListener = new BaseBindingAdapter.ItemClickListener<Answer>() {
    @Override public void onClick(Answer item, int position) {
      presenter.onAnswerClick(item);
    }
  };

  public static final String QUESION_EXTRA = "com.novp.sprytar.game.QuestionExtra";
  public static final String LAST_QUESION_EXTRA = "com.novp.sprytar.game.QuestionExtra.lastQuestion";

  @Inject AnswerPresenter presenter;

  @Inject AnswerAdapter answerAdapter;

  private ActivityAnswerBinding binding;
  private Tracker mTracker;

  public static void startForResult(Activity activity, Question question, boolean isLastQuestion, int requestCode) {
    Intent starter = new Intent(activity, AnswerActivity.class);
    starter.putExtra(QUESION_EXTRA, Parcels.wrap(question));
    starter.putExtra(LAST_QUESION_EXTRA, isLastQuestion);

    activity.startActivityForResult(starter, requestCode);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_answer);

    createAnswerComponent().inject(this);
    presenter.attachView(this);

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Answer Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    initUi();

    Question question = Parcels.unwrap(getIntent().getParcelableExtra(QUESION_EXTRA));
    presenter.setQuestion(question);
  }

  private void initUi() {

    answerAdapter.setItemClickListener(answerClickListener);

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    binding.itemsRecyclerView.setLayoutManager(layoutManager);
    binding.itemsRecyclerView.addItemDecoration(new SimpleDividerDecoration(this, R.drawable.line_divider_transparent));
    binding.itemsRecyclerView.setAdapter(answerAdapter);
  }

  @Override public void showQuestion(Question question, boolean questionAnswered, boolean questionAnsweredCorrectly) {

    binding.idTextView.setText(String.valueOf(question.getId() + 1));
    //binding.questionTextView.setText(question.getText());
    answerAdapter.setQuestionInformation(questionAnswered, questionAnsweredCorrectly);
    answerAdapter.setItems(question.getAnswers());
    answerAdapter.notifyDataSetChanged();

    Fragment fragment = null;

    if (questionAnswered) {
      boolean isLastQuestion = getIntent().getBooleanExtra(LAST_QUESION_EXTRA, false);

      if (isLastQuestion) {
        binding.getStartedButton.setText("Finish!");
      } else {
        binding.getStartedButton.setText(getString(R.string.quiz_next_button));
      }
      binding.getStartedButton.setBackgroundResource(R.drawable.rectangle_quiz_button);
      binding.getStartedButton.setTextColor(getResources().getColor(R.color.colorText));

      binding.itemsRecyclerView.setVisibility(View.GONE);
      binding.feedbackTextView.setVisibility(View.VISIBLE);

      String feedback = "";
      if (!questionAnsweredCorrectly) {
        //                fragment = WrongAnswerFragment.newInstance();
        binding.wrongCharacterImageView.setVisibility(View.VISIBLE);
        feedback = question.getWrongFeedback();
      } else {
        //                fragment = QuestionFragment.newInstance("", false, false, null);
        binding.correctCharacterImageView.setVisibility(View.VISIBLE);
        feedback = question.getCorrectFeedback();
      }
      binding.feedbackTextView.setText(feedback);
      binding.hintImageView.setVisibility(View.GONE);
    } else {
      fragment = QuestionFragment.newInstance(question.getText(), true, false, null);
      binding.wrongCharacterImageView.setVisibility(View.GONE);
      binding.correctCharacterImageView.setVisibility(View.GONE);
      binding.itemsRecyclerView.setVisibility(View.VISIBLE);
      binding.feedbackTextView.setVisibility(View.GONE);

      getSupportFragmentManager().beginTransaction().replace(R.id.placeholder_fragment, fragment).commit();
    }
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
    onBackPressed();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  private AnswerComponent createAnswerComponent() {
    return DaggerAnswerComponent.builder().sessionComponent(getSessionComponent()).build();
  }

  @Override public void showError(String message) {

  }

  public void onNavigationButtonClick(View view) {
    presenter.onNavigationButtonClick();
  }

  public void onShowHintClick(View view) {
    presenter.onShowHintClick();
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

  public static class AnswerAdapter extends BaseBindingAdapter<Answer> {

    private final Context context;
    private boolean questionAnswered;
    private boolean questionAnsweredCorrectly;

    @Inject public AnswerAdapter(Context context) {
      this.context = context;
    }

    public void setQuestionInformation(boolean questionAnswered, boolean questionAnsweredCorrectly) {
      this.questionAnswered = questionAnswered;
      this.questionAnsweredCorrectly = questionAnsweredCorrectly;
    }

    @Override protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
      return ItemAnswerBinding.inflate(inflater, parent, false);
    }

    @Override public void onBindViewHolder(BaseBindingViewHolder holder, int position) {

      Answer answer = items.get(position);

      ItemAnswerBinding binding = (ItemAnswerBinding) holder.binding;
      binding.setAnswer(answer);

      if (questionAnswered) {
        if (answer.isAnswerCorrect()) {
          binding.itemTextView.setBackgroundResource(R.drawable.rectangle_quiz_sprytar_button);
          binding.itemTextView.setTextColor(context.getResources().getColor(R.color.colorText));
        } else {
          binding.itemTextView.setBackgroundResource(R.drawable.rectangle_quiz_gray_button);
          binding.itemTextView.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));
        }
      }
    }
  }
}
