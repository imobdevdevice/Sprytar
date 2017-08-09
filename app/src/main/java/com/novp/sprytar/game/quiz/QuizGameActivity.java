package com.novp.sprytar.game.quiz;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.novp.sprytar.R;
import com.novp.sprytar.data.model.EarnedBadge;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.data.model.Quiz;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.databinding.ActivityQuizGameBinding;
import com.novp.sprytar.databinding.ItemQuestionBinding;
import com.novp.sprytar.game.AnswerImageActivity;
import com.novp.sprytar.game.GameHintDialog;
import com.novp.sprytar.presentation.BaseActivity;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseBindingViewHolder;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

public class QuizGameActivity extends BaseActivity implements QuizGameView, View.OnClickListener {

    public static final String VENUE_EXTRA = "com.novp.sprytar.game.VenueExtra";
    public static final String VENUE_ID_EXTRA = "com.novp.sprytar.game.VenueIdExtra";
    public static final String LOCATION_EXTRA = "com.novp.sprytar.data.model.LocationExtra";
    public static final String QUIZ_EXTRA = "com.novp.sprytar.game.QuizExtra";

    private static final int ANSWER_REQUEST_CODE = 1;

    public static void start(Context context, VenueActivity venueActivity, int venueId, Location
            location, Quiz quiz) {
        Intent starter = new Intent(context, QuizGameActivity.class);
        starter.putExtra(VENUE_EXTRA, Parcels.wrap(venueActivity));
        starter.putExtra(VENUE_ID_EXTRA, venueId);
        starter.putExtra(LOCATION_EXTRA, Parcels.wrap(location));
        starter.putExtra(QUIZ_EXTRA, Parcels.wrap(quiz));

        context.startActivity(starter);
    }

    private GuessView.GuessViewCallback callback = new GuessView.GuessViewCallback() {
        @Override
        public void onNextQuestion(Question question) {
            presenter.setQuestionAnswered(question.getGlobalId(), question.getId(), question.isAnsweredCorrectly());
            presenter.setNextQuestion();
        }

        @Override
        public void onSkipQuestion(Question question) {
            presenter.setQuestionAnswered(question.getGlobalId(), question.getId(), question.isAnsweredCorrectly());
            presenter.setNextQuestion();
        }
    };

    @Inject
    QuizGamePresenter presenter;

    @Inject
    QuestionAdapter questionAdapter;

    private ActivityQuizGameBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout
                .activity_quiz_game);

        createQuizGameComponent().inject(this);

        presenter.attachView(this);

        initUI();

        Intent intent = getIntent();
        VenueActivity venueActivity = Parcels.unwrap(intent.getParcelableExtra(VENUE_EXTRA));
        int venueId = intent.getIntExtra(VENUE_ID_EXTRA, 0);
        Location location = Parcels.unwrap(intent.getParcelableExtra(LOCATION_EXTRA));
        Quiz quiz = Parcels.unwrap(intent.getParcelableExtra(QUIZ_EXTRA));

        //  if (venueActivity == null) {
        //      venueActivity = new VenueActivity();
        //    }
        //    venueActivity.setQuestions(createDummyData());
        presenter.setVenueActivity(venueActivity, venueId, location, quiz);
    }

    private void initUI() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 6);
        binding.itemsRecyclerView.setLayoutManager(layoutManager);
        binding.itemsRecyclerView.setAdapter(questionAdapter);
        binding.helpImageView.setOnClickListener(this);
    }

    private QuizGameComponent createQuizGameComponent() {
        return DaggerQuizGameComponent.builder()
                .sessionComponent(getSessionComponent())
                .build();
    }

    @Override
    public void showVenueActivity(VenueActivity venueActivity, Question question) {

        questionAdapter.setCurrentQuestion(question);
        questionAdapter.setItems(venueActivity.getQuestions());
        binding.itemsRecyclerView.scrollToPosition(question.getId());

        binding.guessView.setCallback(callback);
        binding.guessView.setQuestion(question);

    }

    @Override
    public void setQuestions(List<Question> questions) {
        questionAdapter.setCurrentQuestion(questions.get(0));
        questionAdapter.setItems(questions);
        binding.itemsRecyclerView.scrollToPosition(questions.get(0).getId());

        binding.guessView.setCallback(callback);
        binding.guessView.setQuestion(questions.get(0));
    }

    @Override
    public void setNextQuestion(Question question, int questionNumber) {
        if (questionNumber <= questionAdapter.getItemCount() - 1) {
            questionAdapter.setCurrentQuestion(question);
            binding.itemsRecyclerView.scrollToPosition(questionNumber);

            binding.guessView.setQuestion(question);
            questionAdapter.notifyDataSetChanged();
        }

        if (questionNumber == questionAdapter.getItemCount() - 1) {
            binding.guessView.setNextButtonText("Finish");
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }


    @Override
    public void onFinishQuiz(Location location, VenueActivity venueActivity,
                             EarnedBadge badge, int correctAnswers, int numOfQuestions) {
        QuizGameFinishedActivity.startForResult(this, 1, badge, location, correctAnswers, numOfQuestions);
        finish();
    }

    @Override
    public void onClick(View view) {
        presenter.showHint();
    }

    @Override
    public void showHint(String hint) {
        GameHintDialog fragment = GameHintDialog.newInstance(hint);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fragment, null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showImageQuestion(VenueActivity venueActivity, Question question) {
        AnswerImageActivity.startForResult(this, question, ANSWER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ANSWER_REQUEST_CODE) {
            presenter.setNextQuestion();
        }
    }

    public static class QuestionAdapter extends BaseBindingAdapter<Question> {
        private final Context context;
        private Question currentQuestion;

        @Inject
        public QuestionAdapter(Context context) {
            this.context = context;

        }

        public void setCurrentQuestion(Question question) {
            this.currentQuestion = question;
        }

        @Override
        protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
            return ItemQuestionBinding.inflate(inflater, parent, false);
        }

        @Override
        public void onBindViewHolder(BaseBindingViewHolder holder, int position) {
            Question question = items.get(position);

            ItemQuestionBinding binding = (ItemQuestionBinding) holder.binding;
            binding.idTextView.setText(String.valueOf(question.getId() + 1));

            if (question.isAnswered() || question.equals(currentQuestion)) {
                binding.idTextView.setBackgroundResource(R.drawable.bg_question_active_circle_48dp);
                binding.idTextView.setTextColor(context.getResources().getColor(R.color
                        .colorText));
            } else {
                binding.idTextView.setBackgroundResource(R.drawable
                        .bg_question_inactive_circle_48dp);
                binding.idTextView.setTextColor(context.getResources().getColor(R.color
                        .colorPrimaryText));
            }
        }
    }

}
