package com.sprytar.android.location;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sprytar.android.databinding.ItemLocationBinding;
import com.sprytar.android.data.model.Amenity;
import com.sprytar.android.data.model.Location;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.presentation.BaseBindingViewHolder;
import com.sprytar.android.util.ui.AmenityAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LocationListAdapter extends BaseBindingAdapter<Location> {

    private Context context;

    @Inject
    public LocationListAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemLocationBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(BaseBindingViewHolder holder, int position) {
        Location location = items.get(position);
        ItemLocationBinding binding = (ItemLocationBinding) holder.binding;
        binding.setLocation(items.get(position));
        binding.typeTextView.setText(location.getTypeName());

        binding.offlineImageView.setVisibility(location.isOfflineAccess() ? View.VISIBLE : View
                .GONE);

        binding.locationTypeImageView.setImageResource(location.getIcon());

        AmenityAdapter amenityAdapter = new AmenityAdapter(context);
        List<Amenity> amenities = location.getAmenities();
        if (amenities.size() == 0 ) {
            binding.amenitiesRecyclerView.setVisibility(View.GONE);
        } else {
            binding.amenitiesRecyclerView.setVisibility(View.VISIBLE);

            GridLayoutManager layoutManager = new GridLayoutManager(context, 10);
            binding.amenitiesRecyclerView.setLayoutManager(layoutManager);
            binding.amenitiesRecyclerView.setAdapter(amenityAdapter);

            ArrayMap<Integer, Amenity> map = new ArrayMap<>();
            //SparseArray<Amenity> sparseArray = new SparseArray<>()
            for (int i = 0; i < amenities.size(); i++) {
                Amenity amenity = amenities.get(i);
                map.put(amenity.getIcon(), amenity);
            }

            amenities = new ArrayList<Amenity>(map.values());
            amenityAdapter.setItems(amenities);
        }
    }
}
