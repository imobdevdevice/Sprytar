package com.sprytar.android.support;

import com.sprytar.android.injection.PerFragment;
import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@PerFragment
@Component(dependencies = SessionComponent.class)
public interface SupportComponent {
    void inject(SupportFragment supportFragment);
}
