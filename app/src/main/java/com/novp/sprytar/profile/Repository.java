package com.novp.sprytar.profile;

import com.novp.sprytar.data.model.ProfileEarnedBadges;
import com.novp.sprytar.data.model.ProfilePlayedGame;
import com.novp.sprytar.data.model.ProfileVisitedVenue;

import java.util.List;

import rx.Observable;

public interface Repository {

    Observable<List<ProfileVisitedVenue>> getVisitedVenues(int userId);

    Observable<List<ProfileEarnedBadges>> getEarnedBadges(int userId);

    Observable<List<ProfilePlayedGame>> getPlayedGames(int userId);
}
