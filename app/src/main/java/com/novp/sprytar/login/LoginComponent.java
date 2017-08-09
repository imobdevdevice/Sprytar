package com.novp.sprytar.login;

import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@LoginScope
@Component(dependencies = ApplicationComponent.class, modules = LoginModule.class)
public interface LoginComponent {
    void inject(LoginActivity loginActivity);
}
