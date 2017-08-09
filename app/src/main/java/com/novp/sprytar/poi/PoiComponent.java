package com.novp.sprytar.poi;

import com.novp.sprytar.game.TreasureHuntActivity;
import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.ApplicationComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class)
public interface PoiComponent {
    void inject(PoiActivity activity);
}
