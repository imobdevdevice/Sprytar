package com.novp.sprytar.profile;

import com.novp.sprytar.injection.SessionScope;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@ProfileScope
@Component(dependencies = SessionComponent.class, modules = {ProfileModule.class})
public interface ProfileComponent {
    void inject(ProfileFragment fragment);
}
