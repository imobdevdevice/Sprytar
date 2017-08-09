package com.novp.sprytar.data.model;


import com.google.gson.annotations.SerializedName;

public class PlayedGame {

    @SerializedName("user_id")
    public long userId;

    @SerializedName("venue_id")
    public long venueId;

    @SerializedName("game_id")
    public long gameId;

    @SerializedName("game_type_id")
    public int gameTypeId;

    @SerializedName("action_id")
    public int actionId;

    @SerializedName("id")
    public long id;

    @SerializedName("correct_answers")
    public int correctAnswers;
}
