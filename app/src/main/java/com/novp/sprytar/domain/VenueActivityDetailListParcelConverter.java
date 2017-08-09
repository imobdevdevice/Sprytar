package com.novp.sprytar.domain;

import android.os.Parcel;

import com.novp.sprytar.data.model.VenueActivityDetail;

import org.parceler.Parcels;

public class VenueActivityDetailListParcelConverter extends RealmListParcelConverter<VenueActivityDetail> {

    @Override
    public void itemToParcel(VenueActivityDetail input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public VenueActivityDetail itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(VenueActivityDetail.class.getClassLoader()));
    }
}
