package com.novp.sprytar.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.novp.sprytar.R;
import com.novp.sprytar.databinding.ItemBadgeBinding;

public class BadgeAdapter extends BaseAdapter {
    private BadgesPagerFragment fragment;
    private Context mContext;

    public BadgeAdapter(BadgesPagerFragment fragment, Context c) {
        this.fragment = fragment;
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            ItemBadgeBinding binding = ItemBadgeBinding.inflate
                    (LayoutInflater.from(parent.getContext()), parent, false);

            convertView = binding.getRoot();
            holder = new ViewHolder(binding);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            holder.binding.badgeImageView.setImageResource(R.drawable.ic_park_180dp);
        } else if(position == 1) {
            holder.binding.badgeImageView.setImageResource(R.drawable.ic_coffee_brown_48dp);
        } else if(position == 2) {
            holder.binding.badgeImageView.setImageResource(R.drawable.ic_house_active_48dp);
        } else if(position == 10) {
            holder.binding.badgeImageView.setImageResource(R.drawable.ic_game_48dp);

        }

        return convertView;
    }

    // references to our images
    private int[] mThumbIds = new int[20];

    static class ViewHolder {
        final ItemBadgeBinding binding;

        public ViewHolder(ItemBadgeBinding binding) {
            this.binding = binding;
        }

    }
}
