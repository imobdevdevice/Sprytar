package com.novp.sprytar.game;

import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.ApplicationComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class)
public interface QuizGroupComponent {
    void inject(QuizGroupFragment fragment);
}
