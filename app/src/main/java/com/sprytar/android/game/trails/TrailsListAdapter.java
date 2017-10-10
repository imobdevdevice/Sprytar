package com.sprytar.android.game.trails;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sprytar.android.R;
import com.sprytar.android.databinding.ItemSubTrailBinding;
import com.sprytar.android.data.model.SubTrail;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.presentation.BaseBindingViewHolder;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class TrailsListAdapter extends BaseBindingAdapter<SubTrail> {

    private Context context;

    @Inject
    public TrailsListAdapter(Context context){
        this.context = context;
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemSubTrailBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(BaseBindingViewHolder holder, int position) {
        SubTrail subTrail = items.get(position);
        ItemSubTrailBinding binding = (ItemSubTrailBinding) holder.binding;
        binding.title.setText(subTrail.getName());
        binding.info.setText(prepareInfo(subTrail));

        if(subTrail.getWheelchairAccess() == 0){
            binding.wheelchairSupport.setImageDrawable(context.getResources().getDrawable(R.drawable.trails_no_wh));
        }else {
            binding.wheelchairSupport.setImageDrawable(context.getResources().getDrawable(R.drawable.trails_wh));
        }

        if(subTrail.getFilePath() != null){
            if(!subTrail.getFilePath().isEmpty()){
                Picasso.with(context)
                        .load(subTrail.getFilePath())
                        .error(context.getResources().getDrawable(R.drawable.placeholder_trails_icon))
                        .into(binding.image1);
            }else {
                binding.image1.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder_trails_icon));
            }
        }else {
            binding.image1.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder_trails_icon));
        }
    }

    private String prepareInfo(SubTrail subTrail){
        StringBuilder builder = new StringBuilder();
        builder.append(subTrail.getDistance())
                .append(" meters")
                .append(" \u2022 ")
                .append(subTrail.getTypicalTime())
                .append("min")
                .append(" \u2022 ")
                .append(subTrail.getDifficulty())
                .append(" \u2022 ");

        return builder.toString();
    }
}
