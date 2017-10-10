package com.sprytar.android.presentation;


import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;

public abstract class BasePresenterPage {

    protected ViewDataBinding view;
    private AppCompatActivity activity;

    public BasePresenterPage(ViewDataBinding view) {
        this.view = view;
        this.activity = activity;
    }

    public ViewDataBinding getView() {
        return view;
    }

    public abstract void onCreateView();

}