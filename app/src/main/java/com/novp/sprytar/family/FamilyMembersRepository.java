package com.novp.sprytar.family;

import com.novp.sprytar.data.model.FamilyMember;

import java.util.List;

import rx.Observable;

public interface FamilyMembersRepository {

    Observable<List<Integer>> getAvatarList();

    Observable<List<FamilyMember>> getFamilyMembers();

}
