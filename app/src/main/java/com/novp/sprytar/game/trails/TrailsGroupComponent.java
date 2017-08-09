package com.novp.sprytar.game.trails;

import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.ApplicationComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class)
public interface TrailsGroupComponent {
    void inject(TrailsGroupFragment fragment);
}
