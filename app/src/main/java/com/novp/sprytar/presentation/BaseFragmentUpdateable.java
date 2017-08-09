package com.novp.sprytar.presentation;

import android.support.v4.app.Fragment;

import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.injection.component.SessionComponent;
import com.novp.sprytar.util.ui.UpdateableFragment;

public abstract class BaseFragmentUpdateable extends BaseFragment implements UpdateableFragment{
    public static final String UPDATE_PARAM_1 = "com.novp.sprytar.presentation" +
            ".BaseFragmentUpdateable.param1";

}
