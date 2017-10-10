package com.sprytar.android.venuesetup;

import com.sprytar.android.data.model.Location;
import com.sprytar.android.data.model.LocationSetup;
import com.sprytar.android.presentation.MvpView;

public interface VenueSetupView extends MvpView {

    void showError(String message);

    void showDialogMessage(String message);

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showLocationDetails(Location location, LocationSetup locationSetup);
}
