package com.novp.sprytar.profile;

import android.content.Context;

import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.data.model.ProfileEarnedBadges;
import com.novp.sprytar.data.model.ProfilePlayedGame;
import com.novp.sprytar.data.model.ProfileVisitedVenue;
import com.novp.sprytar.presentation.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfilePresenter extends BasePresenter<ProfileView>  {

    private final Context context;
    private final Repository repository;
    private final SpSession spSession;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();


    @Inject
    ProfilePresenter(Context context, Repository repository, SpSession spSession) {
        this.context = context;
        this.repository = repository;
        this.spSession = spSession;
    }

    @Override
    public void attachView(ProfileView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void onDestroyed() {
    }

    public void loadData() {

        compositeSubscription.add(repository.getEarnedBadges(spSession.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ProfileEarnedBadges>>() {
                    @Override
                    public void call(List<ProfileEarnedBadges> items) {
                        getMvpView().updateEarnedBadgesUi(items);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                }));

        compositeSubscription.add(repository.getVisitedVenues(spSession.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ProfileVisitedVenue>>() {
                    @Override
                    public void call(List<ProfileVisitedVenue> visitedVenues) {
                        getMvpView().updateVisitedVenuesUi(visitedVenues);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                }));

        compositeSubscription.add(repository.getPlayedGames(spSession.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ProfilePlayedGame>>() {
                    @Override
                    public void call(List<ProfilePlayedGame> items) {
                        getMvpView().updatePlayedGamesUi(items);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                }));
    }

}
