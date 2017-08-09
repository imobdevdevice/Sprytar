package com.novp.sprytar.game.trails;

import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface TrailsMapComponent {
    void inject(TrailsMapFragment fragment);
}
