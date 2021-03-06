package com.novp.sprytar.game;

import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface AnswerComponent {
    void inject(AnswerActivity answerActivity);
}
