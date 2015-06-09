package com.alxdev.spotifystreamerapp.rxbus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;


public class RxBus {
    private final Subject<Object, Object> bus = new SerializedSubject(PublishSubject.create());

    public void send(Object obj) {
        bus.onNext(obj);

    }

    public Observable<Object> toObserverable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }


}
