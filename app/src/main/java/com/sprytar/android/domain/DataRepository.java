package com.sprytar.android.domain;


import android.net.Uri;
import android.support.v4.util.ArrayMap;

import com.sprytar.android.data.model.Faq;
import com.sprytar.android.data.model.GameRule;
import com.sprytar.android.data.model.Location;

import java.util.List;

import rx.Observable;

public interface DataRepository {

    Observable<List<Location>> getLocations();

    Observable<List<Faq>> getFaqData();

    Observable<ArrayMap<String, Uri>> getAvatarList();

    Observable<List<GameRule>> getGameRulesList();
}
