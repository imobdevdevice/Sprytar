package com.novp.sprytar.data.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.novp.sprytar.domain.AnswerListParcelConverter;
import com.novp.sprytar.domain.VenueActivityDetailListParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.util.List;

import io.realm.QuestionRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = { QuestionRealmProxy.class }, value = Parcel.Serialization.BEAN, analyze = { Question.class })
public class Question extends RealmObject {

    @SerializedName("question_id")
    public int id;

    @PrimaryKey
    @SerializedName(value = "global_question_id", alternate = {"id"})
    public int globalId;

    @SerializedName("question_title")
    public String text;

    @SerializedName("options")
    public RealmList<Answer> answers;

    @SerializedName("question_type")
    public String questionType;

    public boolean answered;

    public boolean answeredCorrectly;

    @SerializedName("correct_feedback")
    public String correctFeedback;

    @SerializedName("wrong_feedback")
    public String wrongFeedback;

    public boolean imageRecognitionQuestion;

    @SerializedName("question_thumbnail")
    public String imagePath;

    @SerializedName("question_clue")
    public String hint;

    @SerializedName("question_lat")
    double latitude;

    @SerializedName("question_lng")
    double longitude;

    @SerializedName("question_elevation")
    String altitude;

    @SerializedName("question_points")
    int questionPoints;

    @SerializedName("photo_hunt_photo")
    String photoHuntUrl;

    @Ignore
    LatLng latLng;

    public Question() {
    }

//    public Question(int id, String text) {
//        this.id = id;
//        this.text = text;
//    }

//    public Question(int id, String text, List<Answer> answers) {
//        this.id = id;
//        this.text = text;
//        this.answers = answers;
//    }
//
//    public Question(int id, String text, List<Answer> answers, String hint) {
//        this.id = id;
//        this.text = text;
//        this.answers = answers;
//        this.hint = hint;
//    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGlobalId() {
        return globalId;
    }

    public void setGlobalId(int globalId) {
        this.globalId = globalId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    @ParcelPropertyConverter(AnswerListParcelConverter.class)
    public void setAnswers(RealmList<Answer> answers) {
        this.answers = answers;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public boolean isAnsweredCorrectly() {
        return answeredCorrectly;
    }

    public void setAnsweredCorrectly(boolean answeredCorrectly) {
        this.answeredCorrectly = answeredCorrectly;
    }

    public LatLng getLatLng() {
        if (latitude == 0.0f || longitude == 0.0f ) {
            return null;
        }
        if (latLng == null) {
            latLng = new LatLng(latitude, longitude);
        }
        return latLng;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isImageRecognitionQuestion() {
        return questionType.equalsIgnoreCase("image");
    }

    public boolean isPhotoHuntQuestion(){
        return questionType.equalsIgnoreCase("photo-hunt");
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getQuestionPoints() {
        return questionPoints;
    }

    public void setQuestionPoints(int questionPoints) {
        this.questionPoints = questionPoints;
    }

    public void setImageRecognitionQuestion(boolean imageRecognitionQuestion) {
        this.imageRecognitionQuestion = imageRecognitionQuestion;
    }

    public String getCorrectFeedback() {
        return correctFeedback;
    }

    public void setCorrectFeedback(String correctFeedback) {
        this.correctFeedback = correctFeedback;
    }

    public String getWrongFeedback() {
        return wrongFeedback;
    }

    public void setWrongFeedback(String wrongFeedback) {
        this.wrongFeedback = wrongFeedback;
    }

    public String getPhotoHuntUrl() {
        return photoHuntUrl;
    }

    public void setPhotoHuntUrl(String photoHuntUrl) {
        this.photoHuntUrl = photoHuntUrl;
    }
}
