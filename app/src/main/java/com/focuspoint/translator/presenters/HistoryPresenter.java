package com.focuspoint.translator.presenters;


import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.HistoryScreenContract;
import com.focuspoint.translator.screen.Navigator;

import java.lang.ref.WeakReference;
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
//        subscriptions.add(translationInteractor.getOnTranslateSubject()
//                .subscribe(
//                        t -> {},//load(),
//                        this::handleError));
//

        subscriptions.add(translationInteractor
                .getHistory()
                .observeOn(AndroidSchedulers.mainThread())
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
}
