package com.novp.sprytar.support;

import com.novp.sprytar.presentation.MvpView;

public interface SupportView extends MvpView {

    void showError(String message);

    void setWidgetVisibility(boolean isLogedIn);

}
