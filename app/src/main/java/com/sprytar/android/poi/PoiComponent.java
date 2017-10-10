package com.sprytar.android.poi;

import com.sprytar.android.injection.PerActivity;
import com.sprytar.android.injection.component.ApplicationComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class)
public interface PoiComponent {
    void inject(PoiActivity activity);
}
