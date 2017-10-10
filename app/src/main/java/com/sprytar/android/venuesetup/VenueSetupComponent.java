package com.sprytar.android.venuesetup;

import com.sprytar.android.injection.component.SessionComponent;
import com.sprytar.android.location.LocationModule;
import com.sprytar.android.setup.SetupScope;

import dagger.Component;

@SetupScope
@Component(dependencies = SessionComponent.class, modules = {LocationModule.class})
public interface VenueSetupComponent {
    void inject(VenueSetupFragment fragment);
}
