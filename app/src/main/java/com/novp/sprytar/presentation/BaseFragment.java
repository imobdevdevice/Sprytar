package com.novp.sprytar.presentation;

import android.support.v4.app.Fragment;

import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.injection.component.SessionComponent;
import com.novp.sprytar.util.ui.UpdateableFragment;

public abstract class BaseFragment extends Fragment {

    protected ApplicationComponent getApplicationComponent() {
        return SprytarApplication.get(getContext()).getComponent();
    }

    protected SessionComponent getSessionComponent() {
        return SprytarApplication.get(getContext()).getSessionComponent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        RefWatcher refWatcher  = SprytarApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }
}
