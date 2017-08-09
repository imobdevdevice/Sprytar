package com.novp.sprytar.injection.component;

import com.novp.sprytar.injection.ServiceScope;
import com.novp.sprytar.services.LocationSendToServerService;

import dagger.Component;

@ServiceScope
@Component(dependencies = SessionComponent.class)
public interface LocationServiceComponent {
    void inject(LocationSendToServerService service);
}
