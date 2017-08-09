package com.novp.sprytar.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.model.AuthUser;
import com.novp.sprytar.data.model.FamilyMember;
import com.novp.sprytar.util.Utils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SpSession {
    private static final String PREF_NAME = "com.novp.sprytar.data.SessionManager";

    private static final String FIRST_RUN_KEY = "first_run";
    private static final String SHOW_DISCLAIMER = "show_disclaimer";
    private static final String SHOW_INTRO = "show_intro";
    private static final String SHOW_INTRO_TREASURE_HUNT = "show_intro_treasurehunt";

    private static final String ADMIN_KEY = "admin";
    private static final String IS_LOGGED_KEY = "is_logged_in";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String ID_KEY = "user_id";
    private static final String NAME_KEY = "name";
    private static final String AGE_KEY = "age";
    private static final String AVATAR_KEY = "avatar";
    private static final String TOKEN_KEY = "token";

    private static final String CURRENT_LOCATION_LONGITUDE_KEY = "longitude";
    private static final String CURRENT_LOCATION_LATITUDE_KEY = "latitude";

    private static final String QUIZ_EXPLAINER_COMPLETED = "quiz_explainer_completed";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    @SuppressLint("CommitPrefEdits")
    @Inject
    public SpSession(Context context) {

        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();

        checkUpdateFirstRun();

    }

    private void checkUpdateFirstRun() {
        boolean firstRun = pref.getBoolean(FIRST_RUN_KEY, true);
        if (firstRun) {
            editor.putBoolean(FIRST_RUN_KEY, false);
            editor.putBoolean(SHOW_DISCLAIMER, true);
            editor.putBoolean(SHOW_INTRO, true);

            editor.apply();
        }
    }

    public void createSession(AuthUser authUser) {

        editor.putString(TOKEN_KEY, authUser.getToken());
        editor.putBoolean(IS_LOGGED_KEY, true);
        editor.putString(EMAIL_KEY, authUser.getEmail());
        editor.putString(PASSWORD_KEY, authUser.getPassword());
        editor.putBoolean(ADMIN_KEY, authUser.isAdmin());
        editor.putString(NAME_KEY, authUser.getDisplayName());
        editor.putInt(ID_KEY, (int) authUser.getId());

        //editor.putString(AVATAR_KEY, PjUtils.userAvatar(authUser));

        editor.apply();
    }

    public void updateSessionData(FamilyMember familyMember) {

        editor.putInt(ID_KEY, familyMember.getId());
        editor.putString(AVATAR_KEY, familyMember.getAvatarUri().toString());
        editor.putString(NAME_KEY, familyMember.getName());
        editor.putInt(AGE_KEY, Utils.getAge(familyMember.getBirthday()));

        editor.apply();
    }

    public void setEmail(String email) {
        editor.putString(EMAIL_KEY, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_KEY, false);
    }

    public String getPassword() {
        return pref.getString(PASSWORD_KEY, null);
    }

    public String getEmail() {
        return pref.getString(EMAIL_KEY, null);
    }

    public String getName() {
        return pref.getString(NAME_KEY, null);
    }

    public int getUserId() {
        return pref.getInt(ID_KEY, -1);
    }

    public int getAge() {
        return pref.getInt(AGE_KEY, -1);
    }

    public String getAvatar() {
        return pref.getString(AVATAR_KEY, null);
    }

    public String getToken() {
        return pref.getString(TOKEN_KEY, null);
    }

    public boolean showDisclaimer() {
        return pref.getBoolean(SHOW_DISCLAIMER, false);
    }

    public void setShowDisclaimerFalse() {
        editor.putBoolean(SHOW_DISCLAIMER, false);
        editor.apply();
    }

    public boolean showIntro() {
        return pref.getBoolean(SHOW_INTRO, false);
    }

    public void setShowIntroFalse() {
        editor.putBoolean(SHOW_INTRO, false);
        editor.apply();
    }


    public boolean showIntroTreasure() {
        return pref.getBoolean(SHOW_INTRO_TREASURE_HUNT, true);
    }

    public void setShowIntroTreasureFalse() {
        editor.putBoolean(SHOW_INTRO_TREASURE_HUNT, false);
        editor.apply();
    }

    public void logout() {
        editor.clear().apply();
        ((SprytarApplication) context).releaseSessionComponent();
    }

    public void setCurrentLocation(Location location) {
        editor.putString(CURRENT_LOCATION_LATITUDE_KEY, Double.toString(location.getLatitude()));
        editor.putString(CURRENT_LOCATION_LONGITUDE_KEY, Double.toString(location.getLongitude()));
        editor.apply();
    }

    public String getCurrentLocationLatitude() {
        return pref.getString(CURRENT_LOCATION_LATITUDE_KEY, "51.4769941");
    }

    public String getCurrentLocationLongitude() {
        return pref.getString(CURRENT_LOCATION_LONGITUDE_KEY, "-0.0013609");
    }

    public boolean isExplainerCompleted() {
        return pref.getBoolean(QUIZ_EXPLAINER_COMPLETED, false);
    }

    public void setQuizExplainerCompleted(boolean isCompleted) {
        editor.putBoolean(QUIZ_EXPLAINER_COMPLETED, isCompleted);
        editor.apply();
    }

    public boolean isAdmin() {
        return pref.getBoolean(ADMIN_KEY, false);
    }
}
