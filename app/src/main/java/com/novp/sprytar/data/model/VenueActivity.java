package com.novp.sprytar.data.model;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.annotations.SerializedName;
import com.novp.sprytar.R;
import com.novp.sprytar.domain.GameRuleListParcelConverter;
import com.novp.sprytar.domain.QuestionListParcelConverter;
import com.novp.sprytar.domain.VenueActivityDetailListParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.VenueActivityRealmProxy;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {VenueActivityRealmProxy.class}, value = Parcel.Serialization.BEAN,
        analyze = {VenueActivity.class})
public class VenueActivity extends RealmObject {

    public static final int TREASURE_HUNT = 1;
    public static final int FITNESS = 2;
    public static final int GUIDED_TOURS = 3;
    public static final int QUIZ_GAME = 4;
    public static final int TRAILS_GAME = 5;

    @PrimaryKey
    @SerializedName("game_id")
    int id;

    @Type
    @SerializedName("game_type_id")
    private int gameTypeId;

    @SerializedName("game_title")
    String name;

    String description;

    @SerializedName("game_points")
    int gamePoints;

    String snippet;

    @SerializedName("game_lat")
    double latitude;

    @SerializedName("game_lng")
    double longitude;

    @Ignore
    LatLng latLng;

    @Ignore
    MarkerOptions markerOptions;

    RealmList<VenueActivityDetail> details;

    RealmList<GameRule> rules;

    RealmList<Question> questions;

    String jsonQestionsFilename;

    @SerializedName("game_type_icon")
    String gameTypeIcon;

    @SerializedName("game_badge_icon")
    String gameBadgeIcon;

    @SerializedName("game_badge_id")
    int gameBadgeId;

    @SerializedName("game_badge_name")
    String gameBadgeName;

    public VenueActivity() {
    }

    private VenueActivity(int id, int gameTypeId, String name, String description) {
        this.id = id;
        this.gameTypeId = gameTypeId;
        this.name = name;
        this.description = description;
    }

    public static class Builder {
        private int id;
        private int gameTypeId;
        private String name;
        private String description;

        public Builder setId(@NonNull int id) {
            this.id = id;
            return this;
        }

        public Builder setType(@NonNull @Type int gameTypeId) {
            this.gameTypeId = gameTypeId;
            return this;
        }

        public Builder setName(@NonNull String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(@NonNull String description) {
            this.description = description;
            return this;
        }

        public VenueActivity build() {
            return  new VenueActivity(id, gameTypeId, name, description);
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        switch (gameTypeId) {
            case TREASURE_HUNT:
                return "Treasure hunt";
            case FITNESS:
                return "Fitness classes";
            case GUIDED_TOURS:
                return "Guided tours";
            default:
                return "";
        }
    }

    public int getIcon() {
        switch (gameTypeId) {
            case TREASURE_HUNT:
                return R.drawable.ic_treasure_hunt_180dp;
            case FITNESS:
                return R.drawable.ic_fitness_class_180dp;
            case GUIDED_TOURS:
                return R.drawable.ic_guided_tour_180dp;
            case QUIZ_GAME:
                return R.drawable.ic_quiz_game_180dp;
            case TRAILS_GAME:
                return R.drawable.ic_trails_game_180dp;
            default:
                return R.drawable.ic_treasure_hunt_180dp;
        }
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

    public MarkerOptions getMarkerOptions() {
        if (markerOptions != null) {
            return markerOptions;
        }

        if (getLatLng() != null) {
            markerOptions = new MarkerOptions()
                    .position(getLatLng())
                    .title(name);
            return markerOptions;
        } else {
            return null;
        }
    }

    public List<VenueActivityDetail> getDetails() {
        return details;
    }

    @ParcelPropertyConverter(VenueActivityDetailListParcelConverter.class)
    public void setDetails(RealmList<VenueActivityDetail> details) {
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGamePoints() {
        return gamePoints;
    }

    public void setGamePoints(int gamePoints) {
        this.gamePoints = gamePoints;
    }

    public List<GameRule> getRules() {
        return rules;
    }

    @ParcelPropertyConverter(GameRuleListParcelConverter.class)
    public void setRules(RealmList<GameRule> rules) {
        this.rules = rules;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    @ParcelPropertyConverter(QuestionListParcelConverter.class)
    public void setQuestions(RealmList<Question> questions) {
        this.questions = questions;
    }

    public String getJsonQestionsFilename() {
        return jsonQestionsFilename;
    }

    public void setJsonQestionsFilename(String jsonQestionsFilename) {
        this.jsonQestionsFilename = jsonQestionsFilename;
    }

    public String getGameTypeIcon() {
        return gameTypeIcon;
    }

    public void setGameTypeIcon(String gameTypeIcon) {
        this.gameTypeIcon = gameTypeIcon;
    }

    public String getGameBadgeIcon() {
        return gameBadgeIcon;
    }

    public int getBadgeIcon() {
        switch (gameTypeId) {
            case TREASURE_HUNT:
                return R.drawable.ic_game_active_24dp;
            case FITNESS:
                return R.drawable.ic_leaf_active_48dp;
            case GUIDED_TOURS:
                return R.drawable.ic_fruit_active_24dp;
            default:
                return R.drawable.ic_game_active_24dp;
        }
    }

    public void setGameBadgeIcon(String gameBadgeIcon) {
        this.gameBadgeIcon = gameBadgeIcon;
    }

    public String getGameBadgeName() {
        return gameBadgeName;
    }

    public void setGameBadgeName(String gameBadgeName) {
        this.gameBadgeName = gameBadgeName;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setMarkerOptions(MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(int gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public int getGameBadgeId() {
        return gameBadgeId;
    }

    public void setGameBadgeId(int gameBadgeId) {
        this.gameBadgeId = gameBadgeId;
    }

    @IntDef({TREASURE_HUNT, FITNESS, GUIDED_TOURS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

}
