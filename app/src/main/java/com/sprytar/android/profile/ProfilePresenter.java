package com.sprytar.android.profile;

import android.content.Context;

import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.ProfileEarnedBadges;
import com.sprytar.android.data.model.ProfilePlayedGame;
import com.sprytar.android.data.model.ProfileVisitedVenue;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfilePresenter extends BasePresenter<ProfileView> {


    private final Repository repository;
    private final SpSession spSession;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();


    @Inject
    ProfilePresenter(Repository repository, SpSession spSession) {
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

    public void loadData(Context context) {
           if(Utils.isNetworkAvailable(context)){
                getMvpView().showLoadingIndicator();
                compositeSubscription.add(repository.getEarnedBadges(spSession.getUserId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<ProfileEarnedBadges>>() {
                            @Override
                            public void call(List<ProfileEarnedBadges> items) {
                                getMvpView().updateEarnedBadgesUi(items);
                                getMvpView().hideLoadingIndicator();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if(throwable instanceof SocketTimeoutException){
                                    getMvpView().showNetworkErrorDialog(true);
                                }else{
                                    getMvpView().showNetworkErrorDialog(false);
                                }
                                getMvpView().hideLoadingIndicator();
                            }
                        }));

                compositeSubscription.add(repository.getVisitedVenues(spSession.getUserId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<ProfileVisitedVenue>>() {
                            @Override
                            public void call(List<ProfileVisitedVenue> visitedVenues) {
                                getMvpView().updateVisitedVenuesUi(visitedVenues);
                                getMvpView().hideLoadingIndicator();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if(throwable instanceof SocketTimeoutException){
                                    getMvpView().showNetworkErrorDialog(true);
                                }else{
                                    getMvpView().showNetworkErrorDialog(false);
                                }
                                getMvpView().hideLoadingIndicator();
                            }
                        }));

                compositeSubscription.add(repository.getPlayedGames(spSession.getUserId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<ProfilePlayedGame>>() {
                            @Override
                            public void call(List<ProfilePlayedGame> items) {
                                getMvpView().updatePlayedGamesUi(items);
                                getMvpView().hideLoadingIndicator();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if(throwable instanceof SocketTimeoutException){
                                    getMvpView().showNetworkErrorDialog(true);
                                }else{
                                    getMvpView().showNetworkErrorDialog(false);
                                }
                                getMvpView().hideLoadingIndicator();
                            }
                        }));
            }else{
                getMvpView().showNetworkErrorDialog(true);
            }
        }
}
