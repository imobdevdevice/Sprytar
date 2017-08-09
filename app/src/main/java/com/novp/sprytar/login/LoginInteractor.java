package com.novp.sprytar.login;


import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.data.model.AuthUser;
import com.novp.sprytar.domain.DataRepository;
import com.novp.sprytar.domain.Interactor;
import com.novp.sprytar.domain.LoginErrorException;
import com.novp.sprytar.domain.NotEnoughParametersException;
import com.novp.sprytar.injection.module.RxModule;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.util.AdminHelper;
import com.novp.sprytar.util.AdminHelperImpl;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;

public class LoginInteractor extends Interactor {

    private final DataRepository dataRepository;
    private final  LoginService loginService;
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
