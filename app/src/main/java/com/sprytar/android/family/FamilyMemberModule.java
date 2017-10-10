package com.sprytar.android.family;

import com.sprytar.android.network.SpService;

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
