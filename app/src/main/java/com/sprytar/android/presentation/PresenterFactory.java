package com.sprytar.android.presentation;


/**
 * We want our presenters to survive configuration changes. Thus we use Loaders to achieve this.
 * https://medium.com/@czyrux/presenter-surviving-orientation-changes-with-loaders-6da6d86ffbbf
 * (from Android Weekly #195)
 * Creates a Presenter object.
 * @param <T> presenter type
 */
public interface PresenterFactory<T extends MvpPresenter> {
    T create();
}
