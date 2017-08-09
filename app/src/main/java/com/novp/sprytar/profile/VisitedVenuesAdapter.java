package com.novp.sprytar.profile;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.novp.sprytar.R;
import com.novp.sprytar.data.model.ProfileVisitedVenue;
import com.novp.sprytar.databinding.ItemLocationPhotoBinding;

import java.util.ArrayList;
import java.util.List;

public class VisitedVenuesAdapter extends BaseAdapter {

    private final StyleSpan bss = new StyleSpan(Typeface.BOLD);

    private List<ProfileVisitedVenue> items;

    public VisitedVenuesAdapter() {
        items = new ArrayList<ProfileVisitedVenue>();
    }

    public void setItems(List<ProfileVisitedVenue> items) {
        this.items = items;
    }

    public int getCount() {
        return items.size();
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

            ItemLocationPhotoBinding binding = ItemLocationPhotoBinding.inflate
                    (LayoutInflater.from(parent.getContext()), parent, false);

            convertView = binding.getRoot();
            holder = new ViewHolder(binding);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProfileVisitedVenue item = items.get(position);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(item.getImagePath())
                .setOldController(holder.binding.photoImageView.getController())
                .build();
        holder.binding.photoImageView.setController(controller);

        String stringNumVisited = " - " + Integer.toString(item.getTimesVisited()) + "-x";
        SpannableStringBuilder spanDescription = new SpannableStringBuilder();
        spanDescription.append(item.getName());
        spanDescription.setSpan(bss, 0, spanDescription.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanDescription.append(stringNumVisited);
        holder.binding.titleTextView.setText(spanDescription);

        return convertView;
    }


    static class ViewHolder {
        final ItemLocationPhotoBinding binding;

        public ViewHolder(ItemLocationPhotoBinding binding) {
            this.binding = binding;
        }

    }
}
