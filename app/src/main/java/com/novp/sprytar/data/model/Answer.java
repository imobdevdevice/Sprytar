package com.novp.sprytar.data.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.AnswerRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = { AnswerRealmProxy.class }, value = Parcel.Serialization.BEAN, analyze = { Answer.class })
public class Answer extends RealmObject {

    public int id;

    @SerializedName("text")
    public String text;

    @SerializedName("correct")
    public boolean answerCorrect;

    public Answer() {
    }

    public Answer(int id, String text, boolean answerCorrect) {
        this.id = id;
        this.text = text;
        this.answerCorrect = answerCorrect;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isAnswerCorrect() {
        return answerCorrect;
    }

    public void setAnswerCorrect(boolean answerCorrect) {
        this.answerCorrect = answerCorrect;
    }

}
