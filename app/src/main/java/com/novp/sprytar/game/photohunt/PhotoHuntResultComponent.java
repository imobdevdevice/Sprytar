package com.novp.sprytar.game.photohunt;

import com.novp.sprytar.game.GameScope;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@GameScope
@Component(dependencies = SessionComponent.class)
public interface PhotoHuntResultComponent {
    void inject(PhotoHuntResultActivity activity);
}
