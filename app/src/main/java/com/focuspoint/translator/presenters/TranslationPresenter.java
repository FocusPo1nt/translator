package com.focuspoint.translator.presenters;

import com.focuspoint.translator.interactors.interfaces.IErrorInteractor;
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
    private IErrorInteractor errorInteractor;
    private CompositeSubscription subscriptions;
    private WeakReference<TranslationScreenContract.View> view;

    private boolean inputChanged; //TODO try without this state;


    public TranslationPresenter(ITranslationInteractor translationInteractor, ILanguageInteractor languageInteractor,
                                IErrorInteractor errorInteractor){
        this.translationInteractor = translationInteractor;
        this.languageInteractor = languageInteractor;
        this.errorInteractor = errorInteractor;

    }

    @Override
    public void attach(TranslationScreenContract.View v) {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        subscriptions = new CompositeSubscription();
        this.view = new WeakReference<>(v);

        subscriptions.add(translationInteractor.getOnTranslateSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showTranslation, this::handleError));

        subscriptions.add(translationInteractor.getOnSourceSubject()
                .subscribe(translation -> view.get().showSource(translation.getSourceLanguage())));

        subscriptions.add(translationInteractor.getOnTargetSubject()
                .subscribe(translation -> view.get().showTarget(translation.getTargetLanguage())));

        subscriptions.add(translationInteractor.getOnFavoriteSubject()
                .subscribe(translation -> view.get().showAddToFavorites(translation.isFavorite())));

        subscriptions.add(errorInteractor.getErrorObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleError, this::handleError));
    }

    @Override
    public void detach() {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        view.clear();
    }

    @Override
    public void onInputChanged(String text) {
        inputChanged = true;
        subscriptions.add(translationInteractor.onInputChanged(text)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEach(t -> inputChanged=false)
                .subscribe(
                        translation -> {},
                        this::handleError));
    }

    @Override
    public void loadTranslation() {
        subscriptions.add(translationInteractor.getLastTranslation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(translation -> {}, this::handleError);
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
        System.out.println("from presenter" + e.toString());
        if (e instanceof UnknownHostException){
            view.get().showError();
        }
    }

    private void showTranslation(Translation translation){


        if (!inputChanged || view.get().getInput().isEmpty()){
            view.get().showInput(translation.getInput());
        }
        view.get().showOutput(translation.getOutputWithWatermark() + "\n\n" + translation.getDictionaryWithWatermark());
        view.get().showSource(translation.getSourceLanguage());
        view.get().showTarget(translation.getTargetLanguage());
        view.get().showAddToFavorites(translation.isFavorite());

        if (translation.getOutput().isEmpty()) view.get().hideMenu();
    }

    @Override
    public void share() {
        subscriptions.add(translationInteractor.getLastTranslation()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> view.get().share(t.getOutput()), this::handleError));
    }
}
