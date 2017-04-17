package com.focuspoint.translator.presenters;


import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.HistoryScreenContract;
import com.focuspoint.translator.screen.Navigator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
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


        subscriptions.add(translationInteractor
                .getHistory()
                .observeOn(AndroidSchedulers.mainThread())
                .map(t -> searchFilter(t, view.getSearch()))
                .subscribe(
                        translations -> this.view.get().showHistory(translations),
                        this::handleError)
        );
    }

    private void handleError(Throwable error) {
        System.out.println(error);
    }

    @Override
    public void setFavorite(Translation translation, boolean favorite) {
        translationInteractor.setFavorite(translation, favorite);
    }

    @Override
    public void load() {
        subscriptions.add(translationInteractor
                .getHistory()
                .observeOn(AndroidSchedulers.mainThread())
                .map(t -> searchFilter(t, view.get().getSearch()))
                .first()
                .subscribe(
                        translations -> this.view.get().showHistory(translations),
                        this::handleError));

    }


    @Override
    public void choose(Translation translation) {
        translationInteractor.setCurrent(translation);
        view.get().goTo(Navigator.SCREEN_TRANSLATION);
    }


    @Override
    public void onSearchChanged(String text) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void deleteTranslation() {

    }


    private List<Translation> searchFilter(List<Translation> translations, String filter) {

        if (filter.isEmpty()) return translations;

        filter = filter.toLowerCase();

        List<Translation> result = new ArrayList<>();
        for (Translation translation : translations) {
            if (translation.getInput().toLowerCase().contains(filter)
                    || translation.getOutput().toLowerCase().contains(filter)) {
                result.add(translation);
            }
        }
        return result;
    }
}
