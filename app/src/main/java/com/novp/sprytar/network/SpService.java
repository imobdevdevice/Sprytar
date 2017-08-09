package com.novp.sprytar.network;

import com.google.gson.JsonObject;
import com.novp.sprytar.data.model.AuthUser;
import com.novp.sprytar.data.model.EarnedBadge;
import com.novp.sprytar.data.model.FamilyMember;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.LocationSetup;
import com.novp.sprytar.data.model.PlayedGame;
import com.novp.sprytar.data.model.ProfileEarnedBadges;
import com.novp.sprytar.data.model.ProfilePlayedGame;
import com.novp.sprytar.data.model.ProfileVisitedVenue;
import com.novp.sprytar.data.model.Quiz;
import com.novp.sprytar.data.model.Trail;
import com.novp.sprytar.data.model.VenueActivity;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

@SuppressWarnings("unused")
public interface SpService {

    String ENDPOINT = "https://cpanel.sprytar.com/api/";

    @GET("get-locations")
    Observable<SpResult<List<Location>>> getLocationList(@Query("lat") double latitude, @Query("lng") double longitude);

    @GET("get-location-details")
    Observable<SpResult<Location>> getLocationDetails(@Query("location_id") String id);

    @GET("get-game-info")
    Observable<SpResult<VenueActivity>> getGameInfo(@Query("game_id") String id);

    @GET("get-offline-games-infos")
    Observable<SpResult<List<VenueActivity>>> getOfflineGames(@Query("location_id") int id);

    @FormUrlEncoded
    @POST("add-children/{id}")
    Observable<SpResult<FamilyMember>> addFamilyMember(@Path("id") int id, @Field("nickname")
            String name, @Field("date_of_birth") long dateOfBirth, @Field("avatar") String avatar);

    @GET("get-children")
    Observable<SpResult<List<FamilyMember>>> getFamilyMembers();


    @Multipart
    @POST("image-recognition")
    Observable<SpResult<Boolean>> checkImage(@Part() MultipartBody.Part image,
                                    @Part("question_id") int id,
                                    @Part("lat") double latitude,
                                    @Part("lng") double longitude);


    @FormUrlEncoded
    @POST("update-account")
    Observable<SpResult<AuthUser>> updateAccount(@Field("email") String email, @Field("password") String
            password, @Field("password_confirmation") String confirmPassword);

    @DELETE("remove-account/{id}")
    Observable<SpResult> removeAccount(@Path("id") int id);

    @FormUrlEncoded
    @POST("support-request")
    Observable<SpResult> sendSupportRequest(@Field("message") String message);

    @FormUrlEncoded
    @POST("fitness-class-request")
    Observable<SpResult> sendFitnessClassRequest(@Field("subject") String subject, @Field
            ("message") String message, @Field("game_id") int id);

    @FormUrlEncoded
    @POST("guided-tour-request")
    Observable<SpResult> sendGuidedTourRequest(@Field("game_id") int id);

    @FormUrlEncoded
    @POST("actions/earned-badge")
    Observable<SpResult<List<EarnedBadge>>> sendEarnedBadge(@Field("user_id") int userId, @Field("badge_id") int badgeId, @Field("venue_id") int venueId);

    @FormUrlEncoded
    @POST("actions/played-game")
    Observable<SpResult<PlayedGame>> sendPlayedGame(@Field("user_id") int userId, @Field("venue_id") int venueId, @Field("game_id") int gameId, @Field("game_type_id") int gameTypeId);

    @FormUrlEncoded
    @POST("actions/answered-question")
    Observable<SpResult> sendAnsweredQuestion(@Field("user_id") int userId, @Field("venue_id") int venueId
            , @Field("game_id") int gameId, @Field("game_type_id") int gameTypeId
            , @Field("question_id") int questionId, @Field("question_status") int questionStatus,@Field("local_question_id") int localQuestionId);

    @FormUrlEncoded
    @POST("actions/visited-venue")
    Observable<SpResult> sendVisitedVenue(@Field("user_id") int userId, @Field("venue_id") int venueId);

    @GET("actions/visited-venue")
    Observable<SpResult<List<ProfileVisitedVenue>>> getVisitedVenues(@Query("user_id") int id);

    @GET("actions/earned-badge")
    Observable<SpResult<List<ProfileEarnedBadges>>> getEarnedBadges(@Query("user_id") int id);

    @GET("actions/played-game")
    Observable<SpResult<List<ProfilePlayedGame>>> getPlayedGames(@Query("user_id") int id);

    @FormUrlEncoded
    @POST("actions/set-current-location")
    Observable<JsonObject> setCurrentLocation(@Field("user_id") int userId, @Field("longitude") double longitude, @Field("latitude") double latitude);

    @GET("get-location-by-admin")
    Observable<SpResult<List<Location>>> getSetupLocationList();

    @GET("get-info-by-venue-id")
    Observable<SpResult<LocationSetup>> getLocationSetup(@Query("location_id") String id);

    @FormUrlEncoded
    @POST("set-current-location-on-ioi-or-question")
    Observable<SpResult> setCurrentLocationIoiQuestion(@Field("id") int id, @Field("type") String type, @Field("longitude") String longitude, @Field("latitude") String latitude);

    @FormUrlEncoded
    @POST("actions/set-device-token")
    Observable<SpResult> setDeviceToken(@Field("user_id") int userId,@Field("token") String token,@Field("device_id") String deviceId);

    @GET("get-quiz-info")
    Observable<SpResult<Quiz>> getQuizDetails(@Query("quiz_id") int quizId);

    @GET("get-trails-info")
    Observable<SpResult<Trail>> getTrailsInfo(@Query("trails_id") int trailsId);

    @FormUrlEncoded
    @POST("save-trails-result")
    Observable<SpResult> saveTrailsResult(@Field("user_id") int userId,@Field("sub_trails_id") long subtrailsId,@Field("passed_point") int passedPoints,@Field("venue_id") int venueId,@Field("trails_id") long trailsId);
}
