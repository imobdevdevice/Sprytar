package com.novp.sprytar.settings;

import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface AccountSettingsComponent {
    void inject(AccountSettingsDialog fragment);
}
