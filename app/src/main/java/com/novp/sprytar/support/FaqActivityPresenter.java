package com.novp.sprytar.support;

import android.support.v7.widget.SearchView;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.novp.sprytar.data.model.Faq;
import com.novp.sprytar.presentation.BasePresenter;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class FaqActivityPresenter extends BasePresenter<FaqActivityView> implements SearchView
.OnQueryTextListener, SearchView.OnCloseListener,
        FaqActivityInteractorSubscriber.SubscriberCallback {

    private final FaqActivityInteractor faqActivityInteractor;

    private List<Faq> faqs = Collections.emptyList();

    private String query;

    @Inject
    FaqActivityPresenter(FaqActivityInteractor faqActivityInteractor) {
        this.faqActivityInteractor = faqActivityInteractor;
    }

    @Override
    public void attachView(FaqActivityView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        faqActivityInteractor.unsubscribe();
    }

    @Override
    public void onDestroyed() {
        faqActivityInteractor.unsubscribe();
    }

    public void loadItems() {
        getMvpView().showLoadingIndicator();
        faqActivityInteractor.execute(new FaqActivityInteractorSubscriber(this));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        query = newText;
        filterFaqs();
        return true;
    }

    @Override
    public boolean onClose() {
        getMvpView().showItems(faqs);
        return true;
    }

    @Override
    public void onItemsReceived(List<Faq> items) {
        this.faqs = items;
    }

    public void filterFaqs() {
        List<Faq> faqList;

        if (!query.isEmpty()) {
            FaqFilter filter = new FaqFilter(query);
            faqList = Lists.newArrayList(Collections2.filter(faqs, filter));
            getMvpView().showItems(faqList);
        } else {
            getMvpView().showItems(faqs);
        }

    }

    public final class FaqFilter implements Predicate<Faq> {
        private final Pattern pattern;

        public FaqFilter(final String query) {
            this.pattern = Pattern.compile(query);
        }

        @Override
        public boolean apply(Faq input) {
            return pattern.matcher(input.getQuestion().toLowerCase()).find();
        }
    }

}
