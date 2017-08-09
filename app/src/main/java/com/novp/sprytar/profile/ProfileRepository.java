package com.novp.sprytar.profile;

import com.novp.sprytar.data.model.ProfileEarnedBadges;
import com.novp.sprytar.data.model.ProfilePlayedGame;
import com.novp.sprytar.data.model.ProfileVisitedVenue;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class ProfileRepository implements Repository {

    private final SpService spService;

    @Inject
    public ProfileRepository(SpService spService) {
        this.spService = spService;
    }

    @Override
    public Observable<List<ProfileVisitedVenue>> getVisitedVenues(int userId) {

        return spService.getVisitedVenues(userId).flatMap(new Func1<SpResult<List<ProfileVisitedVenue>>,
                Observable<List<ProfileVisitedVenue>>>() {
            @Override
            public Observable<List<ProfileVisitedVenue>> call(SpResult<List<ProfileVisitedVenue>> listResponse) {
                if (listResponse.isSuccess()) {
                    List<ProfileVisitedVenue> visitedVenues = listResponse.getData();
                    return Observable.just(visitedVenues);
                } else {
                    return Observable.just(Collections.<ProfileVisitedVenue>emptyList());
                }
            }
        });
    }

    @Override
    public Observable<List<ProfileEarnedBadges>> getEarnedBadges(int userId) {
        return spService.getEarnedBadges(userId).flatMap(new Func1<SpResult<List<ProfileEarnedBadges>>,
                Observable<List<ProfileEarnedBadges>>>() {
            @Override
            public Observable<List<ProfileEarnedBadges>> call(SpResult<List<ProfileEarnedBadges>> listResponse) {
                if (listResponse.isSuccess()) {
                    List<ProfileEarnedBadges> earnedBadges = listResponse.getData();
                    return Observable.just(earnedBadges);
                } else {
                    return Observable.just(Collections.<ProfileEarnedBadges>emptyList());
                }
            }
        });
    }

    @Override
    public Observable<List<ProfilePlayedGame>> getPlayedGames(int userId) {
        return spService.getPlayedGames(userId).flatMap(new Func1<SpResult<List<ProfilePlayedGame>>,
                Observable<List<ProfilePlayedGame>>>() {
            @Override
            public Observable<List<ProfilePlayedGame>> call(SpResult<List<ProfilePlayedGame>> listResponse) {
                if (listResponse.isSuccess()) {
                    List<ProfilePlayedGame> earnedBadges = listResponse.getData();
                    return Observable.just(earnedBadges);
                } else {
                    return Observable.just(Collections.<ProfilePlayedGame>emptyList());
                }
            }
        });
    }
}
