package com.sprytar.android.profile;

import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@ProfileScope
@Component(dependencies = SessionComponent.class, modules = {ProfileModule.class})
public interface ProfileComponent {
    void inject(ProfileFragment fragment);
}
