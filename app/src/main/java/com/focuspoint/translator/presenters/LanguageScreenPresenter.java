package com.focuspoint.translator.presenters;

import com.focuspoint.translator.interactors.LanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.screen.LanguageScreenContract;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by root on 06.04.17.
 */

public class LanguageScreenPresenter implements LanguageScreenContract.Presenter {

    private CompositeSubscription subscriptions;
    private ILanguageInteractor interactor;
    private WeakReference<LanguageScreenContract.View> view;

    public LanguageScreenPresenter(ILanguageInteractor interactor){
        this.interactor = interactor;
    }

    @Override
    public void attach(LanguageScreenContract.View view) {
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
    public void loadSourceLanguages() {
        subscriptions.add(interactor.loadLanguages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(map -> new ArrayList<>(map.values()))
                .subscribe(
                        languages -> view.get().showLanguages(languages),
                        throwable -> view.get().showError(throwable))
        );
    }

    @Override
    public void loadTargetLanguages() {

    }


    @Override
    public void onSourceChanged(Language language) {
        interactor.changeSource(language);
    }

    @Override
    public void onTargetChanged(Language language) {
        interactor.changeTarget(language);
    }
}
