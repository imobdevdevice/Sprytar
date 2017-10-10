package com.sprytar.android.game.quiz;

import com.sprytar.android.game.GameScope;
import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@GameScope
@Component(dependencies = SessionComponent.class)
public interface QuizGameComponent {
    void inject(QuizGameActivity activity);
}
