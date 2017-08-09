package com.novp.sprytar.login;

import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.data.model.FamilyMember;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.location.LocationRepository;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class CloudLoginRepository implements LoginRepository{

    private final SpService service;
    private final SpSession session;

    @Inject
    public CloudLoginRepository(SpService service, SpSession session) {
        this.service = service;
        this.session = session;
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
