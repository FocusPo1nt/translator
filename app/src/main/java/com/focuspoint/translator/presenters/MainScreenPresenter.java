package com.focuspoint.translator.presenters;

import com.focuspoint.translator.interactors.interfaces.TranslationInteractor;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.TranslationScreenContract;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Implementation of TranslationScreenContract interface;
 * Holds current Translation state;
 * getCurrentTranslation is the main method to work with state;
 */

public class MainScreenPresenter implements TranslationScreenContract.Presenter {

    private TranslationInteractor translationInteractor;
    private CompositeSubscription subscriptions;
    private WeakReference<TranslationScreenContract.View> view;
    private Translation currentTranslation;


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
    public void onInputChanged(String text) {
        getCurrentTranslation()
                .map(translation -> translation.setInput(text))
                .doOnNext(translation -> {if (text.isEmpty()) view.get().showOutput("");})
                .filter(translation -> !text.isEmpty())
                .flatMap(translation -> translationInteractor.translate(translation))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(translation -> {
                    view.get().showSource(translation.getSourceLanguage());
                    view.get().showTarget(translation.getTargetLanguage());
                    view.get().showOutput(translation.getOutput());
                }, throwable -> view.get().showError(throwable));
    }

    @Override
    public void loadTranslation() {
        subscriptions.add(getCurrentTranslation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(translation -> {
                    view.get().showSource(translation.getSourceLanguage());
                    view.get().showTarget(translation.getTargetLanguage());
                    view.get().showInput(translation.getInput());
                    view.get().showOutput(translation.getOutput());
                }, throwable -> view.get().showError(throwable))
        );
    }


    /**If current translation state is null -> return last or default state;*/
    private Observable<Translation> getCurrentTranslation(){
        if (currentTranslation != null) {
            return Observable.just(currentTranslation);
        }else{
            return translationInteractor.getLastTranslation()
                    .doOnNext(translation -> currentTranslation = translation);
        }
    }

}
