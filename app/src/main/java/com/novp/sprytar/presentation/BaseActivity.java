package com.novp.sprytar.presentation;

import android.support.v7.app.AppCompatActivity;

import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.injection.component.SessionComponent;

public abstract class BaseActivity extends AppCompatActivity {

    protected ApplicationComponent getApplicationComponent() {
        return ((SprytarApplication) getApplication()).getComponent();
    }

    protected SessionComponent getSessionComponent() {
        return  SprytarApplication.get(this).getSessionComponent();
    }
}
