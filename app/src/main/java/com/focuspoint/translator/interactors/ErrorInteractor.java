package com.focuspoint.translator.interactors;

import com.focuspoint.translator.interactors.interfaces.IErrorInteractor;

import javax.inject.Inject;

import retrofit2.http.PUT;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 */

public class ErrorInteractor implements IErrorInteractor {

    @Inject
    public ErrorInteractor(){
    }

    @Override
    public Observable<Throwable> getErrorObservable() {
        return errorSubject.asObservable();
    }

    @Override
    public void onError(Throwable error) {
        errorSubject.onNext(error);
    }

    private PublishSubject<Throwable> errorSubject = PublishSubject.create();
}
