package com.novp.sprytar.fitness;

import android.content.Context;

import com.novp.sprytar.events.SendNotificationEvent;
import com.novp.sprytar.presentation.BasePresenter;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class FitnessClassesNotifyPresenter extends BasePresenter<FitnessClassesNotifyView> {

    private final Context context;
    private final EventBus bus;

    @Inject
    public FitnessClassesNotifyPresenter(Context context, EventBus eventBus) {
        this.context = context;
        this.bus = eventBus;
    }

    @Override
    public void attachView(FitnessClassesNotifyView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public void onDestroyed() {
    }

    public void onSendClick(String subject, String message) {

        bus.post(new SendNotificationEvent.Builder()
                    .setSubject(subject)
                    .setMessage(message)
                    .build());

        getMvpView().closeDialog();

    }
}
