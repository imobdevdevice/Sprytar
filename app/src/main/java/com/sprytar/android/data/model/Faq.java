package com.sprytar.android.data.model;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel
public class Faq extends RealmObject{

    @PrimaryKey
    int id;

    public String question;

    public String answer;

    public Faq() {
    }

    public Faq(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
