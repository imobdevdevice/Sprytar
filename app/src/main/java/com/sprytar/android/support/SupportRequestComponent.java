package com.sprytar.android.support;


import com.sprytar.android.injection.PerActivity;
import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = SessionComponent.class)
public interface SupportRequestComponent {
    void inject(SupportRequestActivity supportRequestActivity);
}
