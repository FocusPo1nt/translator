package com.focuspoint.translator.presenters;

import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.FavoriteScreenContract;

import java.lang.ref.WeakReference;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 *
 */

public class FavoritePresenter implements FavoriteScreenContract.Presenter{

    private CompositeSubscription subscriptions;

    private WeakReference<FavoriteScreenContract.View> view;
    private ITranslationInteractor translationInteractor;

    public FavoritePresenter(ITranslationInteractor translationInteractor){
        this.translationInteractor = translationInteractor;
    }


    @Override
    public void detach() {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        view.clear();
    }

    @Override
    public void attach(FavoriteScreenContract.View view) {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        subscriptions = new CompositeSubscription();
        this.view = new WeakReference<>(view);
        //        subscriptions.add(translationInteractor.getOnTranslateSubject()
//                .subscribe(
//                        t -> {},//load(),
//                        this::handleError));
//

        subscriptions.add(translationInteractor
                .getFavorites()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        translations -> this.view.get().showFavorites(translations),
                        this::handleError));
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
