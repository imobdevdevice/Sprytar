package com.novp.sprytar.tour;

import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.presentation.MvpView;

interface GuidedToursView extends MvpView {

  void showError(String message);

  void showVenueActivity(VenueActivity venueActivity);

  void showSendSuccessDialog();

  void showDialogMessage(String message);

}
