package com.sprytar.android.presentation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.sprytar.android.SprytarApplication;
import com.sprytar.android.injection.component.ApplicationComponent;
import com.sprytar.android.injection.component.SessionComponent;

public abstract class BasePresenterActivity<P extends MvpPresenter<V>, V extends MvpView> extends AppCompatActivity {

    private static final int LOADER_ID = 101;
    private MvpPresenter<V> presenter;

    protected ApplicationComponent getApplicationComponent() {
        return ((SprytarApplication) getApplication()).getComponent();
    }

    protected SessionComponent getSessionComponent() {
        return  SprytarApplication.get(this).getSessionComponent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportLoaderManager().initLoader(loaderId(), null, new LoaderManager.LoaderCallbacks<P>() {
            @Override
            public final Loader<P> onCreateLoader(int id, Bundle args) {
                return new PresenterLoader<>(BasePresenterActivity.this, getPresenterFactory());
            }

            @Override
            public final void onLoadFinished(Loader<P> loader, P presenter) {
                BasePresenterActivity.this.presenter = presenter;
                onPresenterPrepared(presenter);
            }

            @Override
            public final void onLoaderReset(Loader<P> loader) {
                BasePresenterActivity.this.presenter = null;
                onPresenterDestroyed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.attachView(getPresenterView());
    }

    @Override
    protected void onStop() {
        presenter.detachView();
        super.onStop();
    }

    /**
     * Instance of {@link PresenterFactory} use to create a Presenter when needed. This instance should
     * not contain {@link android.app.Activity} context reference since it will be keep on rotations.
     */
    @NonNull
    protected abstract PresenterFactory<P> getPresenterFactory();

    /**
     * Hook for subclasses that deliver the {@link MvpPresenter} before its View is attached.
     * Can be use to initialize the Presenter or simple hold a reference to it.
     */
    protected abstract void onPresenterPrepared(@NonNull P presenter);

    /**
     * Hook for subclasses before the screen gets destroyed.
     */
    protected void onPresenterDestroyed() {
    }

    /**
     * Override in case of activity not implementing Presenter<View> interface
     */
    @NonNull
    protected V getPresenterView() {
        return (V) this;
    }

    /**
     * Use this method in case you want to specify a spefic ID for the {@link PresenterLoader}.
     * By default its value would be {@link #LOADER_ID}.
     */
    protected int loaderId() {
        return LOADER_ID;
    }
}
