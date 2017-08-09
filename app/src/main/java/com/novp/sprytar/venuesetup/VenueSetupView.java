package com.novp.sprytar.venuesetup;

import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.LocationSetup;
import com.novp.sprytar.presentation.MvpView;

public interface VenueSetupView extends MvpView {

    void showError(String message);

    void showDialogMessage(String message);

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showLocationDetails(Location location, LocationSetup locationSetup);
}
