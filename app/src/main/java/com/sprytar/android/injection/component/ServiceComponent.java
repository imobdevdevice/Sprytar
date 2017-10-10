package com.sprytar.android.injection.component;

import com.sprytar.android.injection.ServiceScope;
import com.sprytar.android.services.GeofenceTransitionsIntentService;
import com.sprytar.android.services.LocationUpdatesIntentService;

import dagger.Component;

@ServiceScope
@Component(dependencies = ApplicationComponent.class)
public interface ServiceComponent {

    void inject(LocationUpdatesIntentService service);

    void inject(GeofenceTransitionsIntentService service);
}
