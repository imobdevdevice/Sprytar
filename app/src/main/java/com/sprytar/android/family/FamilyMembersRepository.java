package com.sprytar.android.family;

import com.sprytar.android.data.model.FamilyMember;

import java.util.List;

import rx.Observable;

public interface FamilyMembersRepository {

    Observable<List<Integer>> getAvatarList();

    Observable<List<FamilyMember>> getFamilyMembers();

}
