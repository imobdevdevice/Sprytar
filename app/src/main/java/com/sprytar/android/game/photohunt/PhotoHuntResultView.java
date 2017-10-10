package com.sprytar.android.game.photohunt;

import com.sprytar.android.presentation.MvpView;

public interface PhotoHuntResultView extends MvpView {

    void nextQuestion();

    void setImage(String imageUri);

    void setCorrectAnswer(boolean isCorrect);
}
