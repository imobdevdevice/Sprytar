package com.sprytar.android.game;

import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@GameScope
@Component(dependencies = SessionComponent.class, modules = {GameModule.class})
public interface TreasureHuntComponent {
    void inject(TreasureHuntActivity treasureHuntActivity);
}
