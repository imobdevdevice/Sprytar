package com.novp.sprytar.data;

import com.novp.sprytar.data.model.Faq;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class InitialData implements Realm.Transaction{

    @Override
    public void execute(Realm realm) {

        List<Faq> faqList = new ArrayList<Faq>();
        faqList.add(new Faq(0, "Who can play?", "The recommended age is from 3 to 103 :) Basically anyone can play, and we encourage everyone to download the app and try out the Augmented Reality brought in the real world by Sprytar."));
        faqList.add(new Faq(1, "Are there any age restrictions?", "The players can be of all ages. But we make sure that the content that children see is properly filtered, safe and approved by the main account, should the “Family” decide to create an account and have “Child” accounts."));
        faqList.add(new Faq(2, "Where can Sprytar be played?", "Sprytar can be played in a large number of parks across the country. We make sure to increase that number week by week and hope to have the magical AR gameplay brought to over 1000 locations around UK by the end of 2017."));
        faqList.add(new Faq(3, "What devices are supported?", "Sprytar is available for both major mobile platforms, iOS and Android. It can be played on any iOS device running iOS 8.0 or above, and any Android device running Android 4.0 or above."));
        faqList.add(new Faq(4, "Do I have to sign up to play?", "No you do not, but we recommend that you do. The app works great even when not logged in, but the benefits of creating an account are great as you can save games, keep your earned badges and points, play offline and many more accolades. You can even choose to give it a go and after you are truly mesmerized by it, sign up right in the app and save all the initial progress that you made."));

        //realm.beginTransaction();
        realm.copyToRealm(faqList);
        //realm.commitTransaction();

    }

}
