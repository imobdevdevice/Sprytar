package com.novp.sprytar.domain;

import com.novp.sprytar.data.model.PoiTrigger;

import org.parceler.Parcels;

public class PoiTriggerListParcelConverter extends RealmListParcelConverter<PoiTrigger> {

    @Override
    public void itemToParcel(PoiTrigger input, android.os.Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public PoiTrigger itemFromParcel(android.os.Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(PoiTrigger.class.getClassLoader()));
    }
}
