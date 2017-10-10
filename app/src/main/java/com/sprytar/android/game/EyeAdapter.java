package com.sprytar.android.game;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.sprytar.android.databinding.ItemEyesBinding;

import java.util.ArrayList;
import java.util.List;

public class EyeAdapter extends BaseAdapter{
    private Context mContext;

    private List<Uri> items;

    public EyeAdapter(Context c) {
        mContext = c;
        items = new ArrayList<>();
    }

    public void setItems(List<Uri> items) {
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

            ItemEyesBinding binding = ItemEyesBinding.inflate
                    (LayoutInflater.from(parent.getContext()), parent, false);

            convertView = binding.getRoot();
            holder = new ViewHolder(binding);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(items.get(position))
                .setOldController(holder.binding.eyesImageView.getController())
                .build();
//        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder
//                (getResources());
//        GenericDraweeHierarchy hierarchy = builder.setActualImageScaleType(ScalingUtils
//                .ScaleType.CENTER_INSIDE).build();
//        binding.questionImageView.setHierarchy(hierarchy);
        holder.binding.eyesImageView.setController(controller);

        return convertView;
    }

    static class ViewHolder {
        final ItemEyesBinding binding;

        public ViewHolder(ItemEyesBinding binding) {
            this.binding = binding;
        }
    }}
