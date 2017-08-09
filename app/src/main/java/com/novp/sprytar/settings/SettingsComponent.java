package com.novp.sprytar.settings;

import com.novp.sprytar.injection.PerFragment;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.support.SupportFragment;

import dagger.Component;

@PerFragment
@Component(dependencies = ApplicationComponent.class)
public interface SettingsComponent {
    void inject(SettingsFragment fragment);
}
