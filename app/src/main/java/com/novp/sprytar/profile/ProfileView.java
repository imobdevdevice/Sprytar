package com.novp.sprytar.profile;

import com.novp.sprytar.data.model.ProfileEarnedBadges;
import com.novp.sprytar.data.model.ProfilePlayedGame;
import com.novp.sprytar.data.model.ProfileVisitedVenue;
import com.novp.sprytar.presentation.MvpView;

import java.util.List;

public interface ProfileView extends MvpView {

    void showError(String message);

    void updateVisitedVenuesUi(List<ProfileVisitedVenue> items);

    void updateEarnedBadgesUi(List<ProfileEarnedBadges> items);

    void updatePlayedGamesUi(List<ProfilePlayedGame> items);
}
