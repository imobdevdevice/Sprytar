package com.sprytar.android.login;

import com.sprytar.android.injection.component.ApplicationComponent;

import dagger.Component;

@LoginScope
@Component(dependencies = ApplicationComponent.class, modules = LoginModule.class)
public interface RegisterComponent {
    void inject(RegisterActivity activity);
}
