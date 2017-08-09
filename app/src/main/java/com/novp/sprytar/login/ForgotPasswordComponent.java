package com.novp.sprytar.login;

import com.novp.sprytar.injection.component.ApplicationComponent;

import dagger.Component;

@LoginScope
@Component(dependencies = ApplicationComponent.class, modules = LoginModule.class)
public interface ForgotPasswordComponent {
    void inject(ForgotPasswordActivity activity);
}
