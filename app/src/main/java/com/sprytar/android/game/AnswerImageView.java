package com.sprytar.android.game;


import android.net.Uri;

import com.sprytar.android.data.model.Question;
import com.sprytar.android.presentation.MvpView;

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
