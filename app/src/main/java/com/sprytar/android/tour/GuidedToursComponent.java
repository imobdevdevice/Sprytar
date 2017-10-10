package com.sprytar.android.tour;

import com.sprytar.android.injection.PerActivity;
import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface GuidedToursComponent {
    void inject(GuidedToursActivity activity);
}
