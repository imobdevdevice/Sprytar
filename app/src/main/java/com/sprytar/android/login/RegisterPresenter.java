package com.sprytar.android.login;

import android.content.Context;
import android.os.AsyncTask;

import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.AuthUser;
import com.sprytar.android.network.SpLoginResult;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.net.HttpHeaders.USER_AGENT;

public class RegisterPresenter extends BasePresenter<RegisterView> {

    private final RegisterInteractor interactor;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private final LoginService loginService;
    private final SpSession spSession;

    @Inject
    RegisterPresenter(RegisterInteractor interactor, LoginService loginService, SpSession spSession) {
        this.interactor = interactor;
        this.loginService = loginService;
        this.spSession = spSession;
    }

    @Override
    public void attachView(RegisterView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        interactor.unsubscribe();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void onDestroyed() {
        interactor.unsubscribe();
    }

    public void onSkipSingInClick() {
        getMvpView().showMainActivity();
    }

    public void registerUser(Context context, final String email, final String password,
                             final String postcode, final String dateOfBirth, final String gender) {

        if(Utils.isNetworkAvailable(context)){
            getMvpView().showLoadingIndicator();
            compositeSubscription.add(loginService
                    .register(email, password, password,postcode,dateOfBirth, gender)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SpLoginResult>() {
                        @Override
                        public void call(SpLoginResult response) {
                            if (response.isSuccess()) {
                                AuthUser authUser = new AuthUser();
                                authUser.setEmail(email);
                                authUser.setPassword(password);
                                authUser.setToken(response.getToken());
                                spSession.createSession(authUser);
                                //  getMvpView().showFamilyMemberUi();
                                getMvpView().showMainActivity();
                            } else {
                                getMvpView().showDialogMessage("Registering error: " + response.getMessage());
                            }
                            getMvpView().hideLoadingIndicator();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            getMvpView().hideLoadingIndicator();
                            if (throwable instanceof SocketTimeoutException) {
                                getMvpView().showErrorDialog(true);
                            }
                            else {
                                getMvpView().showErrorDialog(false);
                            }
                        }
                    }));
        }else{
            getMvpView().showErrorDialog(true);
        }


    }

    public void getAdressesForPostCode(String postcode) {
        if(postcode != null){
            if(!postcode.isEmpty()){
                new GetAdressesTask().execute(postcode);
            }else {
                getMvpView().showError("Postcode is empty");
            }
        }
    }

    private void displayAddressesFromPostCode(List<String> adresses){
        getMvpView().showAddressesFound(adresses);
    }

    private class GetAdressesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            return getPostCodeAddress(strings[0]);
        }

        private String getPostCodeAddress(String postcode) {
            String url = "https://api.getAddress.io/v2/uk/" + postcode +
                    "?api-key=fnTyE0-yckyUw1MXX4Fs_A9034";

            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");

                //add request header
                con.setRequestProperty("User-Agent", USER_AGENT);

                int responseCode = con.getResponseCode();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s != null){
                try {
                    JSONObject result = new JSONObject(s);
                    JSONArray adresses = result.getJSONArray("Addresses");
                    List<String> listOfAdresses = new ArrayList<>();
                    for(int i=0;i<adresses.length();i++){
                        String ob = adresses.get(i).toString();
                        String[] parseOb = ob.split(",");

                        if(parseOb.length > 0){
                            StringBuilder builder = new StringBuilder();
                            builder.append(parseOb[0]).append(",");

                            try {
                                builder.append(parseOb[parseOb.length - 2]).append(",");
                                builder.append(parseOb[parseOb.length - 1]);

                            }catch(Exception e){
                                e.printStackTrace();
                            }

                            listOfAdresses.add(builder.toString());
                        }
                    }
                    adresses = null;
                    result = null;
                    displayAddressesFromPostCode(listOfAdresses);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
