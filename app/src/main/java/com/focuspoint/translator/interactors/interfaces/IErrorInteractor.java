package com.focuspoint.translator.interactors.interfaces;

import rx.Observable;


/**Notify observers that something went wrong;*/
public interface IErrorInteractor {

    Observable<Throwable> getErrorObservable();

    void onError(Throwable error);
}
