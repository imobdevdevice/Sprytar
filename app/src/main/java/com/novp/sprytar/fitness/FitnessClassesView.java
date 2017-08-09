package com.novp.sprytar.fitness;

import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.presentation.MvpView;

interface FitnessClassesView extends MvpView {

  void showError(String message);

  void showVenueActivity(VenueActivity venueActivity);

  void showNotifyDialog();

  void showDialogMessage(String message);

}
