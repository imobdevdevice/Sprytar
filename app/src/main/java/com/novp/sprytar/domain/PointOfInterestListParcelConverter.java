package com.novp.sprytar.domain;

import android.os.Parcel;

import com.novp.sprytar.data.model.PointOfInterest;

import org.parceler.Parcels;

public class PointOfInterestListParcelConverter extends RealmListParcelConverter<PointOfInterest> {

    @Override
    public void itemToParcel(PointOfInterest input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public PointOfInterest itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(PointOfInterest.class.getClassLoader()));
    }
}
