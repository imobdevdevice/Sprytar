package com.sprytar.android.location;

import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@LocationScope
@Component(dependencies = SessionComponent.class, modules = {LocationModule.class})
public interface LocationComponent {
    void inject(LocationFragment locationFragment);
}
