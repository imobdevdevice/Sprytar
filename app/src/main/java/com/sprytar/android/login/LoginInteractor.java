package com.sprytar.android.login;


import com.sprytar.android.util.AdminHelperImpl;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.AuthUser;
import com.sprytar.android.domain.DataRepository;
import com.sprytar.android.domain.Interactor;
import com.sprytar.android.domain.LoginErrorException;
import com.sprytar.android.domain.NotEnoughParametersException;
import com.sprytar.android.injection.module.RxModule;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.util.AdminHelper;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;

public class LoginInteractor extends Interactor {

    private final DataRepository dataRepository;
    private final LoginService loginService;
    private final SpSession spSession;

    @Inject
    public LoginInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                           @Named(RxModule.MAIN) Scheduler mainScheduler,
                           DataRepository repository, LoginService loginService, SpSession spSession) {
        super(workScheduler, mainScheduler);

        this.dataRepository = repository;
        this.loginService = loginService;
        this.spSession = spSession;
    }

    @Override
    public Observable<AuthUser> buildInteractorObservable(final String... params) {
        if (params.length < 2) {
            return Observable.error(new NotEnoughParametersException());
        } else {
            return loginService.login(params[0], params[1]).flatMap(new Func1<SpResult<AuthUser>,Observable<AuthUser>>() {
                @Override
                public Observable<AuthUser> call(SpResult<AuthUser> response) {
                    if (response.isSuccess()) {
                        AuthUser authUser = response.getData();
                        authUser.setPassword(params[1]);
                        AdminHelper adminHelper = new AdminHelperImpl();
                        authUser.setAdmin(adminHelper.isAdmin());
                        authUser.setToken(response.getData().getToken());
                        authUser.setIs_superuser(response.getData().getIs_superuser());
                        spSession.createSession(authUser);
                        return Observable.just(authUser);
                    } else {
                        if (response.getCode() == 60) {
                            return  Observable.error(new LoginErrorException("These credentials " +
                                    "do not match our records."));
                        } else {
                            return  Observable.error(new LoginErrorException());
                        }
                    }
                }
            });
        }
    }
}
