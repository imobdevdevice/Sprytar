package com.novp.sprytar.login;

import com.novp.sprytar.injection.component.ApplicationComponent;

import dagger.Component;

@LoginScope
@Component(dependencies = ApplicationComponent.class)
public interface PostcodeComponent {
    void inject(PostcodeActivity activity);
}
