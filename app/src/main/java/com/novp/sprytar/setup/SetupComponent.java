package com.novp.sprytar.setup;

import com.novp.sprytar.injection.component.SessionComponent;
import com.novp.sprytar.location.LocationModule;

import dagger.Component;

@SetupScope
@Component(dependencies = SessionComponent.class, modules = {LocationModule.class})
public interface SetupComponent {
    void inject(SetupFragment fragment);
}
