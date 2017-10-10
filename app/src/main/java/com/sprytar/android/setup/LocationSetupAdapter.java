package com.sprytar.android.setup;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sprytar.android.databinding.ItemLocationSetupBinding;
import com.sprytar.android.data.model.Location;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.presentation.BaseBindingViewHolder;

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
