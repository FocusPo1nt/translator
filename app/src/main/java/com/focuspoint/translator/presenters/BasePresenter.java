package com.focuspoint.translator.presenters;

/**
 * Base presenter;
 */

public interface BasePresenter<V>  {
    void attach(V view);

    void detach();
}