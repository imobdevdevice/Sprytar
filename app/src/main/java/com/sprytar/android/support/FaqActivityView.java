package com.sprytar.android.support;


import com.sprytar.android.data.model.Faq;
import com.sprytar.android.presentation.BaseView;

interface FaqActivityView extends BaseView<Faq> {

    void showError(String message);

}
