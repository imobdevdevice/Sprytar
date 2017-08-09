package com.novp.sprytar.login;

import com.novp.sprytar.data.model.FamilyMember;

import java.util.List;

import rx.Observable;

public interface LoginRepository {

    Observable<List<FamilyMember>> getFamilyMembers();

}
