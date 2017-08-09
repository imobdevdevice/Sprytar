package com.novp.sprytar.setup;

import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.presentation.BaseView;

public interface SetupView extends BaseView<Location> {

    void showError(String message);

}
