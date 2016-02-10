package com.mtvindia.connect.util;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Farhan Ali
 */

public class RxUtil {

    public static void subscribeForNetwork(CompositeSubscription subscription, Observable observable, Observer observer) {
        subscription.add(
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer));
    }

    public static CompositeSubscription initSubscription(CompositeSubscription subscriptions) {
        if (subscriptions == null || subscriptions.isUnsubscribed()) {
            return new CompositeSubscription();
        }

        return subscriptions;
    }

    public static void unsubscribe(Subscription subscription) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

}
