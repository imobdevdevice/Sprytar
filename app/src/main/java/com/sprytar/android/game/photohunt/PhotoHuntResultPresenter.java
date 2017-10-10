package com.sprytar.android.game.photohunt;

import android.content.Context;

import com.sprytar.android.data.model.Question;
import com.sprytar.android.presentation.BasePresenter;

import javax.inject.Inject;

public class PhotoHuntResultPresenter extends BasePresenter<PhotoHuntResultView> {

    private String imageUri;
    private Question question;

    @Inject
    PhotoHuntResultPresenter(Context context){

    }

    @Override
    public void onDestroyed() {
    }

    public void setData(boolean isCorrectAnswer,String imageUri,Question question){
        this.imageUri = imageUri;
        this.question = question;
        getMvpView().setImage(this.imageUri);
        getMvpView().setCorrectAnswer(isCorrectAnswer);
    }

    public void nextQuestion(){
        getMvpView().nextQuestion();
    }

    public String getQuestionUrl(){
        return question.getPhotoHuntUrl();
    }
}
