package com.sprytar.android.game;


import com.sprytar.android.data.model.Question;
import com.sprytar.android.presentation.MvpView;

interface AnswerView extends MvpView {

    void showError(String message);

    void showQuestion(Question question, boolean questionAnswered, boolean questionAnsweredCorrectly);

    void showHint(String hint);

    void close();

    void nextQuestion();
}
