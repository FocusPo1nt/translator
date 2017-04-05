package com.focuspoint.translator.presenters;

/**
 * Base presenter;
 */

public interface BasePresenter<V>  {
    void subscribe(V view);

    void unSubscribe();
}