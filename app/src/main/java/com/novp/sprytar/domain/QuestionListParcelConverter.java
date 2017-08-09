package com.novp.sprytar.domain;

import android.os.Parcel;

import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.data.model.VenueActivity;

import org.parceler.Parcels;

public class QuestionListParcelConverter extends RealmListParcelConverter<Question> {

    @Override
    public void itemToParcel(Question input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public Question itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Question.class.getClassLoader()));
    }
}
