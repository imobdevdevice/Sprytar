package com.novp.sprytar.game.quiz;

import com.novp.sprytar.game.GameScope;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@GameScope
@Component(dependencies = SessionComponent.class)
public interface QuizGameStartComponent {
    void inject(QuizGameStartActivity activity);
}
