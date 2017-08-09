package com.novp.sprytar.domain;


import android.net.Uri;
import android.support.v4.util.ArrayMap;

import com.novp.sprytar.data.model.Faq;
import com.novp.sprytar.data.model.GameRule;
import com.novp.sprytar.data.model.Location;

import java.util.List;

import rx.Observable;

public interface DataRepository {

    Observable<List<Location>> getLocations();

    Observable<List<Faq>> getFaqData();

    Observable<ArrayMap<String, Uri>> getAvatarList();

    Observable<List<GameRule>> getGameRulesList();
}
