package com.sprytar.android.settings;

import com.sprytar.android.injection.PerActivity;
import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface AccountSettingsComponent {
    void inject(AccountSettingsDialog fragment);
}
