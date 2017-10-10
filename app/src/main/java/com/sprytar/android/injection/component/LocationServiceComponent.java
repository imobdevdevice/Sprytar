package com.sprytar.android.injection.component;

import com.sprytar.android.injection.ServiceScope;
import com.sprytar.android.services.LocationSendToServerService;

import dagger.Component;

@ServiceScope
@Component(dependencies = SessionComponent.class)
public interface LocationServiceComponent {
    void inject(LocationSendToServerService service);
}
