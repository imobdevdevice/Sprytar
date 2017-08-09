package com.novp.sprytar.support;


import com.novp.sprytar.data.model.Faq;
import com.novp.sprytar.presentation.BaseView;

interface FaqActivityView extends BaseView<Faq> {

    void showError(String message);

}
