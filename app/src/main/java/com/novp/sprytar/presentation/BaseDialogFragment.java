package com.novp.sprytar.presentation;

import android.support.v4.app.DialogFragment;

import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.injection.component.SessionComponent;

public abstract class BaseDialogFragment extends DialogFragment {

    protected ApplicationComponent getApplicationComponent() {
        return SprytarApplication.get(getContext()).getComponent();
    }

    protected SessionComponent getSessionComponent() {
        return SprytarApplication.get(getContext()).getSessionComponent();
    }

}
