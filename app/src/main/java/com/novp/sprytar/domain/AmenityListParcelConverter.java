package com.novp.sprytar.domain;

import android.os.Parcel;

import com.novp.sprytar.data.model.Amenity;

import org.parceler.Parcels;

public class AmenityListParcelConverter extends RealmListParcelConverter<Amenity> {

    @Override
    public void itemToParcel(Amenity input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public Amenity itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Amenity.class.getClassLoader()));
    }
}
