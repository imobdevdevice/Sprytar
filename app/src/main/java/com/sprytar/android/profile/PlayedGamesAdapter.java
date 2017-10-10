package com.sprytar.android.profile;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sprytar.android.databinding.ItemPlayedGameBinding;
import com.sprytar.android.data.model.ProfilePlayedGame;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.presentation.BaseBindingViewHolder;

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
