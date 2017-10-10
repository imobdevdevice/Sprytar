package com.sprytar.android.location;

import android.view.View;

import com.sprytar.android.data.model.Location;
import com.sprytar.android.presentation.BaseView;

public interface LocationView extends BaseView<Location> {

    void showError(String message);

    void showErrorDialog(boolean hasNoInternet);

    View getChildFragment();

}
