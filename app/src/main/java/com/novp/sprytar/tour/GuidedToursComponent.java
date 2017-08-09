package com.novp.sprytar.tour;

import com.novp.sprytar.fitness.FitnessClassesActivity;
import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface GuidedToursComponent {
    void inject(GuidedToursActivity activity);
}
