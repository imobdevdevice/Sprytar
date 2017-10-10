package com.sprytar.android.tour;

import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.presentation.MvpView;

interface GuidedToursView extends MvpView {

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showErrorDialog(boolean hasNoInternet);

    void showError(String message);

    void showVenueActivity(VenueActivity venueActivity);

    void showSendSuccessDialog();

    void showDialogMessage(String message);

}
