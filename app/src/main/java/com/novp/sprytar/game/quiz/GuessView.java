package com.novp.sprytar.game.quiz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.novp.sprytar.R;
import com.novp.sprytar.data.model.Answer;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.databinding.ViewGuessBinding;

import java.util.ArrayList;
import java.util.List;

public class GuessView extends LinearLayout implements View.OnClickListener {

    public interface GuessViewCallback {
        void onNextQuestion(Question question);

        void onSkipQuestion(Question question);
    }

    private AnswerView.AnswerViewCallback answerCallback = new AnswerView.AnswerViewCallback() {
        @Override
        public void onCorrectAnswer(Answer answer) {
            question.setAnsweredCorrectly(true);
            setNextButtonLogic(answer,true);
            binding.imageAnswer.setImageDrawable(getContext().getResources().getDrawable(R.drawable.correct_answer_character));
            binding.imageAnswer.setVisibility(View.VISIBLE);
            binding.imageAnswer.startAnimation(bounce);
            binding.answersLayout.removeAllViews();
            binding.feedbackTextView.setVisibility(View.VISIBLE);
            binding.feedbackTextView.setText(question.getCorrectFeedback());
        }

        @Override
        public void onIncorrectAnswer(Answer answer) {
            question.setAnsweredCorrectly(false);
            binding.imageAnswer.setImageDrawable(getContext().getResources().getDrawable(R.drawable.wrong_answer_character));
            binding.imageAnswer.setVisibility(View.VISIBLE);
            binding.imageAnswer.startAnimation(bounce);
            setNextButtonLogic(answer,false);
            binding.answersLayout.removeAllViews();
            binding.feedbackTextView.setVisibility(View.VISIBLE);
            binding.feedbackTextView.setText(question.getWrongFeedback());
        }

        @Override
        public void onSkipQuestion() {
            setNextButtonLogic(null,false);
            if (callback != null) {
                binding.imageAnswer.setVisibility(View.GONE);
                binding.feedbackTextView.setVisibility(View.GONE);
                question.setAnsweredCorrectly(false);
                binding.answersLayout.removeAllViews();
                question.setAnswered(true);
                callback.onSkipQuestion(question);
            }
        }
    };

    private ViewGuessBinding binding;
    private Question question;
    private GuessViewCallback callback;
    private List<AnswerView> answerViews;
    private Animation bounce;

    public GuessView(Context context) {
        super(context);

        inflateLayout();
        setListeners();
        initAnim();
    }

    private void initAnim(){
        bounce = AnimationUtils.loadAnimation(getContext(),
                R.anim.bounce);
    }

    private void setNextButtonLogic(Answer answer, boolean isCorrect){
        binding.btnNextQuesion.setVisibility(View.VISIBLE);
        for(int i=0;i<answerViews.size();i++){
            answerViews.get(i).removeListeners();

            if(answer != null && answerViews.get(i).getAnswer() != null){
                if(answerViews.get(i).getAnswer().equals(answer)){
                    if(isCorrect){
                        answerViews.get(i).changeGreenColor();
                    }
                }else {
                    answerViews.get(i).changeGreyColor();
                }
            }

            if(i == answerViews.size() -1){
                answerViews.get(i).setVisibility(View.GONE);
            }
        }
    }

    public GuessView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflateLayout();
        setListeners();
        initAnim();
    }

    private void inflateLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = ViewGuessBinding.inflate(inflater, this, true);
    }

    public void setQuestion(Question question) {
        this.question = question;

        binding.question.setText(question.getText());

        if (answerViews == null) {
            answerViews = new ArrayList<>();
        }

        answerViews.clear();

        for (int i = 0; i < question.getAnswers().size(); i++) {
            AnswerView answerView = new AnswerView(getContext());
            answerView.setAnswer(question.getAnswers().get(i));
            answerView.setCallback(answerCallback);
            answerViews.add(answerView);
            binding.answersLayout.addView(answerViews.get(i));
        }

        addSkipView();

        binding.btnNextQuesion.setVisibility(View.GONE);
    }

    private void addSkipView() {
        AnswerView skipView = new AnswerView(getContext());
        skipView.setAnswer(null);
        skipView.setCallback(answerCallback);
        skipView.setSkipQuestion(true);
        answerViews.add(skipView);
        binding.answersLayout.addView(skipView);
    }

    public void setNextButtonText(String text) {
        binding.btnNextQuesion.setText(text);
    }

    private void setListeners() {
        binding.btnNextQuesion.setOnClickListener(this);
    }

    public void setCallback(GuessViewCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == binding.btnNextQuesion.getId()) {
            if (callback != null) {
                binding.imageAnswer.setVisibility(View.GONE);
                binding.feedbackTextView.setVisibility(View.GONE);
                binding.answersLayout.removeAllViews();
                question.setAnswered(true);
                callback.onNextQuestion(question);
            }
        }
    }
}
