package com.novp.sprytar.setup;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novp.sprytar.data.model.Amenity;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.databinding.ItemLocationBinding;
import com.novp.sprytar.databinding.ItemLocationSetupBinding;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseBindingViewHolder;
import com.novp.sprytar.util.ui.AmenityAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LocationSetupAdapter extends BaseBindingAdapter<Location> {

    private Context context;

    @Inject
    public LocationSetupAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemLocationSetupBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(BaseBindingViewHolder holder, int position) {
        Location location = items.get(position);
        ItemLocationSetupBinding binding = (ItemLocationSetupBinding) holder.binding;
        binding.setLocation(items.get(position));
        binding.typeTextView.setText(location.getTypeName());

        binding.locationTypeImageView.setImageResource(location.getIcon());

    }
}
