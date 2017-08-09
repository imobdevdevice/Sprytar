package com.novp.sprytar.game.trails;

import com.novp.sprytar.game.GameScope;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@GameScope
@Component(dependencies = SessionComponent.class)
public interface TrailsGameFinishedComponent {
    void inject(TrailsGameFinishedActivity activity);
}
