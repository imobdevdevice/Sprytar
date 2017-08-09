package com.novp.sprytar.presentation;

import java.util.List;

public interface BaseView<I> extends MvpView {

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showItems(List<I> items);

    void clearItems();

    void showMessage(String text);
}
