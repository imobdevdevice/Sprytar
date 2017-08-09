package com.novp.sprytar.profile;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.novp.sprytar.data.model.ProfileEarnedBadges;
import com.novp.sprytar.data.model.ProfilePlayedGame;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.databinding.ItemEarnedBadgeBinding;
import com.novp.sprytar.databinding.ItemPlayedGameBinding;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseBindingViewHolder;
import com.novp.sprytar.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class PlayedGamesAdapter extends BaseBindingAdapter<ProfilePlayedGame> {

    private Context context;

    public PlayedGamesAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemPlayedGameBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(BaseBindingViewHolder holder, int position) {
        ItemPlayedGameBinding binding = (ItemPlayedGameBinding) holder.binding;

        ProfilePlayedGame item = items.get(position);
        binding.setGame(item);
    }
}
