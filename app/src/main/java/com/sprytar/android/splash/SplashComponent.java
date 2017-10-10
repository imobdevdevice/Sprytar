package com.sprytar.android.splash;


import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@SplashScope
@Component(dependencies = SessionComponent.class)
public interface SplashComponent {
    void inject(SplashActivity splashActivity);
}
