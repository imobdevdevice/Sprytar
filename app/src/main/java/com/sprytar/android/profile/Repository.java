package com.sprytar.android.profile;

import com.sprytar.android.data.model.ProfileEarnedBadges;
import com.sprytar.android.data.model.ProfilePlayedGame;
import com.sprytar.android.data.model.ProfileVisitedVenue;

import java.util.List;

import rx.Observable;

public interface Repository {

    Observable<List<ProfileVisitedVenue>> getVisitedVenues(int userId);

    Observable<List<ProfileEarnedBadges>> getEarnedBadges(int userId);

    Observable<List<ProfilePlayedGame>> getPlayedGames(int userId);
}
