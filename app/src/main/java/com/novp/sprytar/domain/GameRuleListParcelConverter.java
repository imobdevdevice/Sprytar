package com.novp.sprytar.domain;

import android.os.Parcel;

import com.novp.sprytar.data.model.GameRule;

import org.parceler.Parcels;

public class GameRuleListParcelConverter extends RealmListParcelConverter<GameRule> {

    @Override
    public void itemToParcel(GameRule input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public GameRule itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(GameRule.class.getClassLoader()));
    }
}
