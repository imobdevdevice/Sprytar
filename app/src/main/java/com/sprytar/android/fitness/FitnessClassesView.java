package com.sprytar.android.fitness;

import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.presentation.MvpView;

interface FitnessClassesView extends MvpView {

  void showLoadingIndicator();

  void hideLoadingIndicator();

  void showErrorDialog(boolean hasNoInternet);

  void showError(String message);

  void showVenueActivity(VenueActivity venueActivity);

  void showNotifyDialog();

  void showDialogMessage(String message);

}
