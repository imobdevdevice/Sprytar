package com.novp.sprytar.game;


import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.presentation.MvpView;

interface AnswerView extends MvpView {

    void showError(String message);

    void showQuestion(Question question, boolean questionAnswered, boolean questionAnsweredCorrectly);

    void showHint(String hint);

    void close();

    void nextQuestion();
}
