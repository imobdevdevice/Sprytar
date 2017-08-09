package com.novp.sprytar.splash;


import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@SplashScope
@Component(dependencies = SessionComponent.class)
public interface SplashComponent {
    void inject(SplashActivity splashActivity);
}
