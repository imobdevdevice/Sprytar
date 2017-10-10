package com.sprytar.android.game;

import com.sprytar.android.injection.PerActivity;
import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface GameMapComponent {
    void inject(GameMapFragment fragment);
}
