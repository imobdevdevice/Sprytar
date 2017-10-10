package com.sprytar.android.game.trails;

import com.sprytar.android.injection.PerActivity;
import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface TrailsMapComponent {
    void inject(TrailsMapFragment fragment);
}
