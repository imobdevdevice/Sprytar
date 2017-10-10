package com.sprytar.android.profile;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sprytar.android.databinding.ItemEarnedBadgeBinding;
import com.sprytar.android.data.model.ProfileEarnedBadges;
import com.sprytar.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class EarnedBadgesAdapter extends BaseAdapter {

    private Context context;
    private List<ProfileEarnedBadges> items;

    public EarnedBadgesAdapter(Context context) {
        items = new ArrayList<ProfileEarnedBadges>();
        this.context = context;
    }

    public void setItems(List<ProfileEarnedBadges> items) {
        this.items = items;
    }

    public int getCount() {
        return items.size();
    }

    public ProfileEarnedBadges getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            ItemEarnedBadgeBinding binding = ItemEarnedBadgeBinding.inflate
                    (LayoutInflater.from(parent.getContext()), parent, false);

            convertView = binding.getRoot();
            holder = new ViewHolder(binding);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ProfileEarnedBadges item = items.get(position);
        Uri uri = null;
        try {
            uri = Utils.getSvgUri(context, item.getName(), item.getIcon());
        } catch(Exception e) {}

//        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setUri(uri)
//                .setOldController(holder.binding.badgeImageView.getController())
//                .build();
//        holder.binding.badgeImageView.setController(controller);

        holder.binding.badgeImageView.setImageURI(uri);

        return convertView;
    }


    static class ViewHolder {
        final ItemEarnedBadgeBinding binding;

        public ViewHolder(ItemEarnedBadgeBinding binding) {
            this.binding = binding;
        }

    }
}
