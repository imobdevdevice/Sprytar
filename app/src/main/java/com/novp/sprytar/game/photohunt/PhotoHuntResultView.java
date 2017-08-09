package com.novp.sprytar.game.photohunt;

import com.novp.sprytar.presentation.MvpView;

public interface PhotoHuntResultView extends MvpView{

    void nextQuestion();

    void setImage(String imageUri);

    void setCorrectAnswer(boolean isCorrect);
}
