package com.sprytar.android.game.trails;

import com.sprytar.android.injection.PerActivity;
import com.sprytar.android.injection.component.ApplicationComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class)
public interface TrailsGroupComponent {
    void inject(TrailsGroupFragment fragment);
}
