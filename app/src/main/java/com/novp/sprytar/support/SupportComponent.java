package com.novp.sprytar.support;

import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.PerFragment;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@PerFragment
@Component(dependencies = SessionComponent.class)
public interface SupportComponent {
    void inject(SupportFragment supportFragment);
}
