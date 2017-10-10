package com.sprytar.android.venue;

import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@VenueScope
@Component(dependencies = SessionComponent.class, modules = {VenueModule.class})
public interface VenueComponent {
    void inject(VenueFragment venueFragment);
}
