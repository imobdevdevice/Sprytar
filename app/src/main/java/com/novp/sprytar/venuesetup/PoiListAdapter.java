package com.novp.sprytar.venuesetup;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novp.sprytar.data.model.PointOfInterest;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.databinding.ItemVenueSetupPoiBinding;
import com.novp.sprytar.databinding.ItemVenueSetupQuestionBinding;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseBindingViewHolder;

import javax.inject.Inject;

public class PoiListAdapter extends BaseBindingAdapter<PointOfInterest> {

    private Context context;

    public interface AdapterCallback {
        void onSetPoiLocation(int position);
    }

    private AdapterCallback adapterCallback;

    @Inject
    public PoiListAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemVenueSetupPoiBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(BaseBindingViewHolder holder, final int position) {
        PointOfInterest poi = items.get(position);
        ItemVenueSetupPoiBinding binding = (ItemVenueSetupPoiBinding) holder.binding;
        binding.setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterCallback.onSetPoiLocation(position);
            }
        });
        binding.setPoi(poi);
    }

    public void setCallback(AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

}
