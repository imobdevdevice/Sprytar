package com.sprytar.android.family;


import com.sprytar.android.injection.component.SessionComponent;

import dagger.Component;

@FamilyMemberScope
@Component(dependencies = SessionComponent.class, modules = {FamilyMemberModule.class})
public interface FamilyMemberComponent {
    void inject(FamilyMemberActivity activity);
}
