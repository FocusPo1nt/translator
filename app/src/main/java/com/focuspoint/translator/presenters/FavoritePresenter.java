package com.focuspoint.translator.presenters;

import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.FavoriteScreenContract;
import com.focuspoint.translator.screen.Navigator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
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
        subscriptions.add(translationInteractor
                .getFavorites()
                .observeOn(AndroidSchedulers.mainThread())
                .map(t -> searchFilter(t, view.get().getSearch()))
                .first()
                .subscribe(
                        translations -> this.view.get().showFavorites(translations),
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

    private List<Translation> searchFilter(List<Translation> translations, String filter){

        if (filter.isEmpty()) return translations;

        filter = filter.toLowerCase();

        List <Translation> result = new ArrayList<>();
        for (Translation translation : translations){
            if (translation.getInput().toLowerCase().contains(filter)
                    || translation.getOutput().toLowerCase().contains(filter)){
                result.add(translation);
            }
        }
        return result;
    }
}
