package com.novp.sprytar.game;

import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@GameScope
@Component(dependencies = SessionComponent.class, modules = {GameModule.class})
public interface TreasureHuntComponent {
    void inject(TreasureHuntActivity treasureHuntActivity);
}
