package com.novp.sprytar.injection.component;

import com.novp.sprytar.injection.ServiceScope;
import com.novp.sprytar.services.GeofenceTransitionsIntentService;
import com.novp.sprytar.services.LocationUpdatesIntentService;

import dagger.Component;

@ServiceScope
@Component(dependencies = ApplicationComponent.class)
public interface ServiceComponent {

    void inject(LocationUpdatesIntentService service);

    void inject(GeofenceTransitionsIntentService service);
}
