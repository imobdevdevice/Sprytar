package com.sprytar.android.domain;

import android.os.Parcel;

import com.sprytar.android.data.model.Answer;

import org.parceler.Parcels;

public class AnswerListParcelConverter extends RealmListParcelConverter<Answer> {

    @Override
    public void itemToParcel(Answer input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public Answer itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Answer.class.getClassLoader()));
    }
}
