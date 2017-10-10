package com.sprytar.android.venuesetup;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sprytar.android.databinding.ItemVenueSetupQuestionBinding;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.presentation.BaseBindingViewHolder;

import javax.inject.Inject;

public class QuestionListAdapter extends BaseBindingAdapter<Question> {

    private Context context;

    public interface AdapterCallback {
        void onSetQuestionLocation(int position);
    }

    private AdapterCallback adapterCallback;

    @Inject
    public QuestionListAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemVenueSetupQuestionBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(BaseBindingViewHolder holder, final int position) {
        Question question = items.get(position);
        ItemVenueSetupQuestionBinding binding = (ItemVenueSetupQuestionBinding) holder.binding;
        binding.setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterCallback.onSetQuestionLocation(position);
            }
        });
        binding.setQuestion(question);
    }

    public void setCallback(AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

}
