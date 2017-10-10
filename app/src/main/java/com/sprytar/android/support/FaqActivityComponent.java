package com.sprytar.android.support;


import com.sprytar.android.injection.PerActivity;
import com.sprytar.android.injection.component.ApplicationComponent;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class)
public interface FaqActivityComponent {
    void inject(FaqActivity faqActivity);
}
