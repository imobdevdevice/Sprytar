package com.novp.sprytar.util.ui;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.novp.sprytar.data.model.Amenity;
import com.novp.sprytar.databinding.ItemAmenityIconBinding;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseBindingViewHolder;

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
