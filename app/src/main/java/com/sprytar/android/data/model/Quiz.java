package com.sprytar.android.data.model;

import com.google.gson.annotations.SerializedName;
import com.sprytar.android.domain.QuestionListParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.util.List;

import io.realm.QuizRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;

@Parcel(implementations = {QuizRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {Quiz.class})
public class Quiz extends RealmObject {

    @SerializedName("quiz_id")
    public int quizId;

    @SerializedName("quiz_type_id")
    public int quizTypeId;

    @SerializedName("quiz_type_name")
    public String quizTypeName;

    @SerializedName("quiz_type_icon")
    public String quizTypeIcon;

    @SerializedName("quiz_min_age")
    public int quizMinAge;

    @SerializedName("quiz_max_age")
    public int quizMaxAge;

    @SerializedName("quiz_title")
    public String quizTitle;

    @SerializedName("quiz_points")
    public int quizPoints;

    @SerializedName("quiz_badge_name")
    public String quizBadgeName;

    @SerializedName("quiz_badge_id")
    public int quizBadgeId;

    @SerializedName("quiz_badge_icon")
    public String quizBadgeIcon;

    @SerializedName("questions")
    RealmList<Question> questions;

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getQuizTypeId() {
        return quizTypeId;
    }

    public void setQuizTypeId(int quizTypeId) {
        this.quizTypeId = quizTypeId;
    }

    public String getQuizTypeName() {
        return quizTypeName;
    }

    public void setQuizTypeName(String quizTypeName) {
        this.quizTypeName = quizTypeName;
    }

    public String getQuizTypeIcon() {
        return quizTypeIcon;
    }

    public void setQuizTypeIcon(String quizTypeIcon) {
        this.quizTypeIcon = quizTypeIcon;
    }

    public int getQuizMinAge() {
        return quizMinAge;
    }

    public void setQuizMinAge(int quizMinAge) {
        this.quizMinAge = quizMinAge;
    }

    public int getQuizMaxAge() {
        return quizMaxAge;
    }

    public void setQuizMaxAge(int quizMaxAge) {
        this.quizMaxAge = quizMaxAge;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public int getQuizPoints() {
        return quizPoints;
    }

    public void setQuizPoints(int quizPoints) {
        this.quizPoints = quizPoints;
    }

    public String getQuizBadgeName() {
        return quizBadgeName;
    }

    public void setQuizBadgeName(String quizBadgeName) {
        this.quizBadgeName = quizBadgeName;
    }

    public int getQuizBadgeId() {
        return quizBadgeId;
    }

    public void setQuizBadgeId(int quizBadgeId) {
        this.quizBadgeId = quizBadgeId;
    }

    public String getQuizBadgeIcon() {
        return quizBadgeIcon;
    }

    public void setQuizBadgeIcon(String quizBadgeIcon) {
        this.quizBadgeIcon = quizBadgeIcon;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public RealmList<Question> getQuestionsRealm() {
        return questions;
    }

    @ParcelPropertyConverter(QuestionListParcelConverter.class)
    public void setQuestions(RealmList<Question> questions) {
        this.questions = questions;
    }
}
