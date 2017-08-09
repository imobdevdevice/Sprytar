package com.novp.sprytar.presentation;


import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseBindingAdapter<I> extends RecyclerView.Adapter<BaseBindingViewHolder> implements BaseBindingViewHolder.ClickListener {

    protected List<I> items = new ArrayList<>();
    private ItemClickListener<I> itemClickListener;

    @Override
    public void onViewClick(int position) {
        if (itemClickListener != null) {
            itemClickListener.onClick(items.get(position), position);
        }
    }

    @Override
    public BaseBindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = bind(inflater, parent, viewType);
        return new BaseBindingViewHolder(binding, this);
    }

    protected abstract ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType);

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<I> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(I item) {
        addItem(item, items.size());
    }

    public void addItem(I item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void addItems(List<I> itemsToAdd) {
        addItems(itemsToAdd, items.size());
    }

    public void addItems(List<I> itemsToAdd, int position) {
        items.addAll(position, itemsToAdd);
        notifyItemRangeInserted(position, itemsToAdd.size());
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int size = items.size();
        if (size > 0) {
            items.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    public void setItemClickListener(ItemClickListener<I> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener<I> {
        void onClick(I item, int position);
    }
}
