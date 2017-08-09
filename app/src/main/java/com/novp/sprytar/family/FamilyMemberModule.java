package com.novp.sprytar.family;

import com.novp.sprytar.network.SpService;

import dagger.Module;
import dagger.Provides;

@Module
public class FamilyMemberModule {

    @Provides
    @FamilyMemberScope
    FamilyMembersRepository provideFamilyMembersRepository(SpService service) {
        return  new CloudFamilyRepository(service);
    }
}
