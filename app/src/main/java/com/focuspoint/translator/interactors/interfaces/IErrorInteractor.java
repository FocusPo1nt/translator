package com.focuspoint.translator.interactors.interfaces;

import rx.Observable;

/**
 *
 */

public interface IErrorInteractor {

    Observable<Throwable> getErrorObservable();

    void onError(Throwable error);



    public class ConnectionException extends IllegalStateException{

    }
}
