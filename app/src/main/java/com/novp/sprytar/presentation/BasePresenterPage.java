package com.novp.sprytar.presentation;


import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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