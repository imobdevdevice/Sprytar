package com.novp.sprytar.venue;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.CharMatcher;
import com.google.common.io.Files;
import com.novp.sprytar.BuildConfig;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.data.model.VenueActivityDetail;
import com.novp.sprytar.databinding.ItemVenueActivityBinding;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseBindingViewHolder;
import com.novp.sprytar.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import javax.inject.Inject;

public class VenueListAdapter extends BaseBindingAdapter<VenueActivity> {

    private Context context;

    @Inject
    public VenueListAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemVenueActivityBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(BaseBindingViewHolder holder, int position) {
        VenueActivity venueActivity = items.get(position);
        ItemVenueActivityBinding binding = (ItemVenueActivityBinding) holder.binding;
        binding.setVenue(items.get(position));
        binding.locationTypeImageView.setImageResource(venueActivity.getIcon());

        List<VenueActivityDetail> details = venueActivity.getDetails();
        binding.distanceTextView.setVisibility(View.GONE);
        binding.gameImageView.setVisibility(View.GONE);
        binding.houseImageView.setVisibility(View.GONE);
        binding.fruitImageView.setVisibility(View.GONE);
        binding.leafImageView.setVisibility(View.GONE);

        binding.fruitImageView.setVisibility(View.VISIBLE);
        Uri uri = null;
        try {
            uri = Utils.getSvgUri(context, venueActivity.getGameBadgeName(), venueActivity.getGameBadgeIcon());
        } catch (IOException ex) {
           uri = getBadgeType(venueActivity);
        }catch (Exception e){
           uri = getBadgeType(venueActivity);
        }

        binding.fruitImageView.setImageURI(uri);
    }

    private Uri getBadgeType(VenueActivity venueActivity){
        try {
            return Utils.getSvgUri(context,Long.toString(System.currentTimeMillis()),venueActivity.getGameTypeIcon());
        } catch (IOException e) {
            return null;
        }
    }

}
