package com.sprytar.android.login;

import com.sprytar.android.data.model.FamilyMember;

import java.util.List;

import rx.Observable;

public interface LoginRepository {

    Observable<List<FamilyMember>> getFamilyMembers();

}
