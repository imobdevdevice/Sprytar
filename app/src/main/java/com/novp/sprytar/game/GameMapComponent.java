package com.novp.sprytar.game;

import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.injection.component.SessionComponent;
import com.novp.sprytar.venue.VenueFragment;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface GameMapComponent {
    void inject(GameMapFragment fragment);
}
