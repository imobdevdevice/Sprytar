package com.sprytar.android.game.photohunt;

import com.sprytar.android.game.GameScope;
import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@GameScope
@Component(dependencies = SessionComponent.class)
public interface PhotoHuntComponent {
    void inject(PhotoHuntActivity activity);
}
