package com.novp.sprytar.fitness;

import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface FitnessClassesComponent {
    void inject(FitnessClassesActivity activity);

    void inject(FitnessClassesNotifyDialog dialog);
}
