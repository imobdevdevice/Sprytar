package com.novp.sprytar.support;


import com.novp.sprytar.injection.PerActivity;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.main.MainActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class)
public interface FaqActivityComponent {
    void inject(FaqActivity faqActivity);
}
