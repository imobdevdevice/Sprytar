package com.sprytar.android.domain;

import com.sprytar.android.data.model.PoiFile;

import org.parceler.Parcels;

public class PoiFileListParcelConverter extends RealmListParcelConverter<PoiFile> {

    @Override
    public void itemToParcel(PoiFile input, android.os.Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public PoiFile itemFromParcel(android.os.Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(PoiFile.class.getClassLoader()));
    }
}
