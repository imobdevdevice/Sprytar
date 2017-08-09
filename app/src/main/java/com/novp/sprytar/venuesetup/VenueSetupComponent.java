package com.novp.sprytar.venuesetup;

import com.novp.sprytar.injection.component.SessionComponent;
import com.novp.sprytar.location.LocationModule;
import com.novp.sprytar.setup.SetupFragment;
import com.novp.sprytar.setup.SetupScope;

import dagger.Component;

@SetupScope
@Component(dependencies = SessionComponent.class, modules = {LocationModule.class})
public interface VenueSetupComponent {
    void inject(VenueSetupFragment fragment);
}
