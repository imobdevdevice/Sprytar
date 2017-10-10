package com.sprytar.android.fitness;

import com.sprytar.android.injection.PerActivity;
import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface FitnessClassesComponent {
    void inject(FitnessClassesActivity activity);

    void inject(FitnessClassesNotifyDialog dialog);
}
