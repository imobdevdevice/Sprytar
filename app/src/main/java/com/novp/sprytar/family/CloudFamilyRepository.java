package com.novp.sprytar.family;


import com.novp.sprytar.data.model.FamilyMember;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class CloudFamilyRepository implements FamilyMembersRepository{

    private final SpService service;

    @Inject
    public CloudFamilyRepository(SpService service) {
        this.service = service;
    }

    @Override
    public Observable<List<Integer>> getAvatarList() {
        return null;
    }

    @Override
    public Observable<List<FamilyMember>> getFamilyMembers() {
        return service.getFamilyMembers().flatMap(new Func1<SpResult<List<FamilyMember>>,
                Observable<List<FamilyMember>>>() {
            @Override
            public Observable<List<FamilyMember>> call(SpResult<List<FamilyMember>> listResponse) {
                if (listResponse.isSuccess()) {
                    return Observable.just(listResponse.getData());
                } else {
                    return Observable.just(Collections.<FamilyMember>emptyList());
                }
            }
        });
    }
}
