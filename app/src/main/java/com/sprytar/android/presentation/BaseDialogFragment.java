package com.sprytar.android.presentation;

import android.support.v4.app.DialogFragment;

import com.sprytar.android.SprytarApplication;
import com.sprytar.android.injection.component.ApplicationComponent;
import com.sprytar.android.injection.component.SessionComponent;

public abstract class BaseDialogFragment extends DialogFragment {

    protected ApplicationComponent getApplicationComponent() {
        return SprytarApplication.get(getContext()).getComponent();
    }

    protected SessionComponent getSessionComponent() {
        return SprytarApplication.get(getContext()).getSessionComponent();
    }

}
