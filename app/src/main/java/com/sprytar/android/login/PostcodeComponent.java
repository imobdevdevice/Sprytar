package com.sprytar.android.login;

import com.sprytar.android.injection.component.ApplicationComponent;

import dagger.Component;

@LoginScope
@Component(dependencies = ApplicationComponent.class)
public interface PostcodeComponent {
    void inject(PostcodeActivity activity);
}
