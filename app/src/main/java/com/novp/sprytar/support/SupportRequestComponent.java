package com.novp.sprytar.support;


import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface SupportRequestComponent {
    void inject(SupportRequestActivity supportRequestActivity);
}
