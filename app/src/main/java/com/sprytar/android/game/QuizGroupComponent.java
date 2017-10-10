package com.sprytar.android.game;

import com.sprytar.android.injection.PerActivity;
import com.sprytar.android.injection.component.ApplicationComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class)
public interface QuizGroupComponent {
    void inject(QuizGroupFragment fragment);
}
