package com.sprytar.android.support;

import com.sprytar.android.presentation.MvpView;

public interface SupportView extends MvpView {

    void showError(String message);

    void setWidgetVisibility(boolean isLogedIn);

}
