package com.focuspoint.translator.presenters;

import android.widget.Toast;

import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.TranslationScreenContract;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Implementation of TranslationScreenContract interface;
 */

public class TranslationPresenter implements TranslationScreenContract.Presenter {

    private ITranslationInteractor translationInteractor;
    private ILanguageInteractor languageInteractor;
    private CompositeSubscription subscriptions;
    private WeakReference<TranslationScreenContract.View> view;



    public TranslationPresenter(ITranslationInteractor translationInteractor, ILanguageInteractor languageInteractor){
        this.translationInteractor = translationInteractor;
        this.languageInteractor = languageInteractor;
    }

    @Override
    public void attach(TranslationScreenContract.View v) {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        subscriptions = new CompositeSubscription();
        this.view = new WeakReference<>(v);

        subscriptions.add(translationInteractor.getOnTranslateSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showTranslation));

        subscriptions.add(translationInteractor.getOnSourceSubject()
                .subscribe(translation -> view.get().showSource(translation.getSourceLanguage())));

        subscriptions.add(translationInteractor.getOnTargetSubject()
                .subscribe(translation -> view.get().showTarget(translation.getTargetLanguage())));

        subscriptions.add(translationInteractor.getOnFavoriteSubject()
                .subscribe(translation -> view.get().showAddToFavorites(translation.isFavorite())));
    }

    @Override
    public void detach() {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        view.clear();
    }

    @Override
    public void onInputChanged(String text) {
        subscriptions.add(translationInteractor.onInputChanged(text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        translation -> {},
                        this::handleError));
    }

    @Override
    public void loadTranslation() {
        subscriptions.add(translationInteractor.getLastTranslation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(translation -> System.out.println("load in presenter " + translation))
                .doOnError(throwable -> System.out.println("presenter EROROR"))
                .subscribe(
                        this::showTranslation,
                        this::handleError)
        );
    }


    @Override
    public void onSourceChanged(Language language) {
        view.get().showSource(language);
    }

    @Override
    public void onTargetChange(Language language) {
        view.get().showTarget(language);
    }

    @Override
    public void reverseLanguages() {

        translationInteractor.reverseLanguages()
            .subscribe(
                translation -> {},
                throwable -> System.out.println(throwable.toString()));
    }



    @Override
    public void changeFavorites() {
        subscriptions.add(translationInteractor
                .changeCurrentFavorite()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        favorite -> view.get().showAddToFavorites(favorite),
                        this::handleError));
    }


    @Override
    public void clear() {
        translationInteractor.clearCurrent();
    }


    @Override
    public void onKeyboardClose() {
        subscriptions.add(translationInteractor.addCurrentToHistory()
                .subscribe(t -> {}, this::handleError));
    }

    @Override
    public void onExitTranslationScreen() {
        subscriptions.add(translationInteractor.addCurrentToHistory()
                .subscribe(t -> {}, this::handleError));
    }

    private void handleError(Throwable e){
        System.out.println("presenter" + e);
        if (e instanceof UnknownHostException){
            view.get().showConnectionError();
        }
    }

    private void showTranslation(Translation translation){
        view.get().showInput(translation.getInput());
        view.get().showOutput(translation.getOutputWithWatermark());
        view.get().showSource(translation.getSourceLanguage());
        view.get().showTarget(translation.getTargetLanguage());
        view.get().showAddToFavorites(translation.isFavorite());
        if (translation.getOutput().isEmpty()) view.get().hideMenu();
    }



}
