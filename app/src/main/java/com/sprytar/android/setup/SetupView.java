package com.sprytar.android.setup;

import com.sprytar.android.data.model.Location;
import com.sprytar.android.presentation.BaseView;

public interface SetupView extends BaseView<Location> {

    void showError(String message);

}
