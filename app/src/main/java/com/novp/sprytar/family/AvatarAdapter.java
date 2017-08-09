package com.novp.sprytar.family;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.novp.sprytar.R;
import com.novp.sprytar.databinding.ItemAvatarBinding;

import java.util.ArrayList;
import java.util.List;

import static android.R.color.transparent;

public class AvatarAdapter extends BaseAdapter{
    private Context mContext;
    Resources resources;

    private List<Uri> items;
    private int selected;

    public AvatarAdapter(Context c) {
        mContext = c;
        resources =  mContext.getResources();
        items = new ArrayList<>();
    }

    public void setItems(List<Uri> items, int position) {
        this.selected = position;
        this.items = items;
    }

    public void setSelected(int position) {
        this.selected = position;
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

            ItemAvatarBinding binding = ItemAvatarBinding.inflate
                    (LayoutInflater.from(parent.getContext()), parent, false);

            convertView = binding.getRoot();
            holder = new ViewHolder(binding);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(items.get(position))
                .setOldController(holder.binding.avatarImageView.getController())
                .build();
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder
                (resources);

        RoundingParams roundingParams = RoundingParams.fromCornersRadius(15f);
        roundingParams.setBorderWidth(3);
        int overlayColor = resources.getColor(R.color.colorAccent);
        int transparentColor = resources.getColor(transparent);

        if (position == selected) {
            roundingParams.setBorderColor(overlayColor);
            //roundingParams.setOverlayColor(overlayColor);
        } else {
            roundingParams.setBorderColor(transparentColor);
            //roundingParams.setOverlayColor(transparentColor);
        }

        GenericDraweeHierarchy hierarchy = builder
                .setRoundingParams(roundingParams)
                .build();

        holder.binding.avatarImageView.setHierarchy(hierarchy);
       holder.binding.avatarImageView.setController(controller);

        return convertView;
    }

    static class ViewHolder {
        final ItemAvatarBinding binding;

        public ViewHolder(ItemAvatarBinding binding) {
            this.binding = binding;
        }
    }}
