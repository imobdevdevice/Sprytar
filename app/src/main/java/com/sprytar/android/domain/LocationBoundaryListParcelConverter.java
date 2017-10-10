package com.sprytar.android.domain;

import android.os.Parcel;

import com.sprytar.android.data.model.LocationBoundary;

import org.parceler.Parcels;

public class LocationBoundaryListParcelConverter extends RealmListParcelConverter<LocationBoundary> {

    @Override
    public void itemToParcel(LocationBoundary input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public LocationBoundary itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(LocationBoundary.class.getClassLoader()));
    }
}
