package com.focuspoint.translator.presenters;

import com.focuspoint.translator.errors.Errors;
import com.focuspoint.translator.interactors.interfaces.TranslationInteractor;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.LanguageScreenContract;
import com.focuspoint.translator.screen.TranslationScreenContract;

import java.lang.ref.WeakReference;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Implementation of MainScreenPresenter interface;
 */

public class MainScreenPresenter implements TranslationScreenContract.Presenter {

    private TranslationInteractor translationInteractor;
    private CompositeSubscription subscriptions;
    private WeakReference<TranslationScreenContract.View> view;

    public MainScreenPresenter(TranslationInteractor interactor){
        this.translationInteractor = interactor;
    }

    @Override
    public void attach(TranslationScreenContract.View view) {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        subscriptions = new CompositeSubscription();
        this.view = new WeakReference<>(view);
    }


    @Override
    public void detach() {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        view.clear();
    }

    @Override
    public void translate(String text) {

    }

    @Override
    public void loadTranslation() {
        subscriptions.add(translationInteractor.getCurrentTranslation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(translation -> {
                    view.get().showSource(translation.getSourceLanguage());
                    view.get().showTarget(translation.getTargetLanguage());
                }, throwable -> view.get().showError(throwable))
        );
    }
}
