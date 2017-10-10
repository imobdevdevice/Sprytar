package com.sprytar.android.game.photohunt;

import com.sprytar.android.data.model.Question;
import com.sprytar.android.presentation.MvpView;

public interface PhotoHuntView extends MvpView {

    void showError(String message);

    void showHint(String hint);

    void showResultActivity(String imageUri, boolean isAnswerCorrect, Question question);

    void setQuestion(Question question);

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showErrorDialog(boolean hasNoInternet);

    void checkPhotoLocation();
}
