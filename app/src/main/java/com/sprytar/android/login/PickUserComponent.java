package com.sprytar.android.login;

import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@LoginScope
@Component(dependencies = SessionComponent.class, modules = {LoginModule.class})
public interface PickUserComponent {
    void inject(PickUserActivity activity);
}
