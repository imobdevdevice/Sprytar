package com.novp.sprytar.venue;

import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@VenueScope
@Component(dependencies = SessionComponent.class, modules = {VenueModule.class})
public interface VenueComponent {
    void inject(VenueFragment venueFragment);
}
