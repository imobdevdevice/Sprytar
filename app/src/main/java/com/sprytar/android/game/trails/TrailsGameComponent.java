package com.sprytar.android.game.trails;

import com.sprytar.android.game.GameScope;
import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@GameScope
@Component(dependencies = SessionComponent.class)
public interface TrailsGameComponent {
    void inject(TrailsGameActivity activity);
}
