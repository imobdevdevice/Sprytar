package com.novp.sprytar.support;

import com.novp.sprytar.data.model.Faq;

import java.util.List;

import io.realm.RealmResults;
import rx.Subscriber;

class FaqActivityInteractorSubscriber extends Subscriber<RealmResults<Faq>> {

    private FaqActivityPresenter faqActivityPresenter;

    private FaqActivityInteractorSubscriber.SubscriberCallback subscriberCallback;

    public FaqActivityInteractorSubscriber(FaqActivityPresenter faqActivityPresenter) {
        this.faqActivityPresenter = faqActivityPresenter;
        this.subscriberCallback = faqActivityPresenter;
    }

    @Override
    public void onCompleted() {
        faqActivityPresenter.getMvpView().hideLoadingIndicator();
    }

    @Override
    public void onError(Throwable e) {
        faqActivityPresenter.getMvpView().hideLoadingIndicator();
        faqActivityPresenter.getMvpView().showMessage(e.getLocalizedMessage());
    }

    @Override
    public void onNext(RealmResults<Faq> faqs) {
        subscriberCallback.onItemsReceived(faqs);
        faqActivityPresenter.getMvpView().showItems(faqs);
    }

    public interface SubscriberCallback {
        void onItemsReceived(List<Faq> items);
    }

}
