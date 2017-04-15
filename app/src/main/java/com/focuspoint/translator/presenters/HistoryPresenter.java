package com.focuspoint.translator.presenters;

import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.HistoryScreenContract;
import com.focuspoint.translator.screen.LanguageScreenContract;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by root on 15.04.17.
 */

public class HistoryPresenter implements HistoryScreenContract.Presenter{

    private CompositeSubscription subscriptions;

    private WeakReference<HistoryScreenContract.View> view;
    private ITranslationInteractor translationInteractor;

    public HistoryPresenter(ITranslationInteractor translationInteractor){
        this.translationInteractor = translationInteractor;
    }


    @Override
    public void detach() {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        view.clear();
    }

    @Override
    public void attach(HistoryScreenContract.View view) {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        subscriptions = new CompositeSubscription();
        this.view = new WeakReference<>(view);
        subscriptions.add(translationInteractor.getOnTranslateSubject()
                .subscribe(
                        t -> load(),
                        this::handleError));

    }

    private void handleError(Throwable error) {
        System.out.println(error);
    }

    @Override
    public void addFavorite() {

    }

    @Override
    public void removeFavorite() {

    }

    @Override
    public void load() {
        System.out.println("LOAD !!!");
        subscriptions.add(translationInteractor
                .getHistory()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(t -> System.out.println("History on next before view"))
                .subscribe(translations -> view.get().showHistory(translations))
        );
    }
}
