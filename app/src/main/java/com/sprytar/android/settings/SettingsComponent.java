package com.sprytar.android.settings;

import com.sprytar.android.injection.PerFragment;
import com.sprytar.android.injection.component.ApplicationComponent;

import dagger.Component;

@PerFragment
@Component(dependencies = ApplicationComponent.class)
public interface SettingsComponent {
    void inject(SettingsFragment fragment);
}
