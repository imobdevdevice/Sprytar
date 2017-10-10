package com.sprytar.android.setup;

import com.sprytar.android.injection.component.SessionComponent;
import com.sprytar.android.location.LocationModule;

import dagger.Component;

@SetupScope
@Component(dependencies = SessionComponent.class, modules = {LocationModule.class})
public interface SetupComponent {
    void inject(SetupFragment fragment);
}
