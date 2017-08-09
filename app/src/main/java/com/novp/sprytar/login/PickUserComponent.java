package com.novp.sprytar.login;

import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@LoginScope
@Component(dependencies = SessionComponent.class, modules = {LoginModule.class})
public interface PickUserComponent {
    void inject(PickUserActivity activity);
}
