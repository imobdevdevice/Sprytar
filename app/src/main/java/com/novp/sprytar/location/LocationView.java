package com.novp.sprytar.location;

import android.view.View;

import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.presentation.BaseView;

public interface LocationView extends BaseView<Location> {

    void showError(String message);

    View getChildFragment();

}
