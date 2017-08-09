package com.novp.sprytar.location;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novp.sprytar.R;
import com.novp.sprytar.data.model.Amenity;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.databinding.ItemLocationBinding;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseBindingViewHolder;
import com.novp.sprytar.util.ui.AmenityAdapter;

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
