package com.sprytar.android.util.ui;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sprytar.android.databinding.ItemAmenityIconBinding;
import com.sprytar.android.data.model.Amenity;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.presentation.BaseBindingViewHolder;

import javax.inject.Inject;

public class AmenityAdapter extends BaseBindingAdapter<Amenity> {
    private final Context context;

    @Inject
    public AmenityAdapter(Context context) {
        this.context = context;

    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemAmenityIconBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(BaseBindingViewHolder holder, int position) {
        Amenity amenity = items.get(position);

        ItemAmenityIconBinding binding = (ItemAmenityIconBinding) holder.binding;
        binding.amenityImageView.setImageResource(amenity.getIcon());
    }
}
