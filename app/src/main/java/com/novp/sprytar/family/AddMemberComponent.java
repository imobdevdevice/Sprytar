package com.novp.sprytar.family;

import com.novp.sprytar.injection.component.SessionComponent;

import dagger.Component;

@FamilyMemberScope
@Component(dependencies = SessionComponent.class, modules = FamilyMemberModule.class)
public interface AddMemberComponent {
    void inject(AddMemberDialog fragment);
}
