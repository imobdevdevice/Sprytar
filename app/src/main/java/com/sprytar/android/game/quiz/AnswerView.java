package com.sprytar.android.game.quiz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.sprytar.android.R;
import com.sprytar.android.databinding.ViewAnswerBinding;
import com.sprytar.android.data.model.Answer;

public class AnswerView extends LinearLayout implements View.OnClickListener {

    public interface AnswerViewCallback {
        void onCorrectAnswer(Answer answer);

        void onIncorrectAnswer(Answer answer);

        void onSkipQuestion();
    }

    private AnswerViewCallback callback;
    private Answer answer;
    private ViewAnswerBinding binding;
    private boolean isSkipQuestion = false;

    public AnswerView(Context context) {
        super(context);

        inflateLayout();
        setListeners();
    }

    public AnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflateLayout();
        setListeners();
    }

    private void setListeners() {
        binding.btnAnswer.setOnClickListener(this);
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;

        if(answer != null){
            binding.btnAnswer.setText(answer.getText());
        }
    }

    public void setSkipQuestion(boolean isSkipQuestion) {
        this.isSkipQuestion = isSkipQuestion;

        if(isSkipQuestion){
            changeGreyColor();
            binding.btnAnswer.setText("Skip Question");
        }
    }

    public void setCallback(AnswerViewCallback callback) {
        this.callback = callback;
    }

    private void inflateLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = ViewAnswerBinding.inflate(inflater, this, true);
    }

    public void removeListeners() {
        binding.btnAnswer.setOnClickListener(null);
        callback = null;
    }

    public void changeGreyColor() {
        binding.btnAnswer.setBackground(getResources().getDrawable(R.drawable.rectangle_quiz_gray_button));
    }

    public void changeGreenColor() {
        binding.btnAnswer.setBackground(getResources().getDrawable(R.drawable.rectangle_quiz_sprytar_button));
    }

    public Answer getAnswer(){
        return answer;
    }

    @Override
    public void onClick(View view) {
        if (callback != null) {

            if (isSkipQuestion) {
                callback.onSkipQuestion();
            } else if (answer.isAnswerCorrect()) {
                callback.onCorrectAnswer(answer);
            } else {
                callback.onIncorrectAnswer(answer);
            }
        }
    }
}
