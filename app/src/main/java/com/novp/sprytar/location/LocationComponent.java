package com.novp.sprytar.location;

import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@LocationScope
@Component(dependencies = SessionComponent.class, modules = {LocationModule.class})
public interface LocationComponent {
    void inject(LocationFragment locationFragment);
}
