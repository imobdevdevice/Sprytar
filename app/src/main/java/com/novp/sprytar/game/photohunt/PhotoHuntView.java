package com.novp.sprytar.game.photohunt;

import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.presentation.MvpView;

public interface PhotoHuntView extends MvpView{

    void showError(String message);

    void showHint(String hint);

    void showResultActivity(String imageUri,boolean isAnswerCorrect,Question question);

    void setQuestion(Question question);

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void checkPhotoLocation();
}
