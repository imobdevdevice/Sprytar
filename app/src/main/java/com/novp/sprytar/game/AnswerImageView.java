package com.novp.sprytar.game;


import android.net.Uri;

import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.presentation.MvpView;

interface AnswerImageView extends MvpView {

    void showError(String message);

    void showQuestion(Question question, boolean questionAnswered, boolean
            questionAnsweredCorrectly);

    void showHint(String hint);

    void showCorrectAnswerActivity(Uri uri, boolean correct);

    void close();

    void nextQuestion();

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showWaitingDialog();

}
