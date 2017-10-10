package com.sprytar.android.profile;

import com.sprytar.android.data.model.ProfileEarnedBadges;
import com.sprytar.android.data.model.ProfilePlayedGame;
import com.sprytar.android.data.model.ProfileVisitedVenue;
import com.sprytar.android.presentation.MvpView;

import java.util.List;

public interface ProfileView extends MvpView {

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showNetworkErrorDialog(boolean hasNoInternet);

    void showError(String message);

    void updateVisitedVenuesUi(List<ProfileVisitedVenue> items);

    void updateEarnedBadgesUi(List<ProfileEarnedBadges> items);

    void updatePlayedGamesUi(List<ProfilePlayedGame> items);
}
