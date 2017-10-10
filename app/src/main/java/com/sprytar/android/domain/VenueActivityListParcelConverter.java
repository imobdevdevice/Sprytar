package com.sprytar.android.domain;

import android.os.Parcel;

import com.sprytar.android.data.model.VenueActivity;

import org.parceler.Parcels;

public class VenueActivityListParcelConverter extends RealmListParcelConverter<VenueActivity> {

    @Override
    public void itemToParcel(VenueActivity input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public VenueActivity itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(VenueActivity.class.getClassLoader()));
    }
}
