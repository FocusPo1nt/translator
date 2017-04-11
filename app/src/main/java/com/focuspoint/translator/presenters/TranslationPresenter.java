package com.focuspoint.translator.presenters;

import com.focuspoint.translator.interactors.LanguageInteractor;
import com.focuspoint.translator.interactors.TranslationInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.TranslationScreenContract;

import java.lang.ref.WeakReference;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Implementation of TranslationScreenContract interface;
 */

public class TranslationPresenter implements TranslationScreenContract.Presenter {

    private TranslationInteractor translationInteractor;
    private LanguageInteractor languageInteractor;
    private CompositeSubscription subscriptions;
    private WeakReference<TranslationScreenContract.View> view;



    public TranslationPresenter(TranslationInteractor translationInteractor, LanguageInteractor languageInteractor){
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
                .doOnNext(new Action1<Translation>() {
                    @Override
                    public void call(Translation translation) {
                        System.out.println("I NEED THIS");
                    }
                })
                .subscribe(translation -> view.get().showOutput(translation.getOutputWithWatermark()),

                        throwable -> {

                        }, () -> {
                            System.out.println("ON COMPLETED");
                        }));

        subscriptions.add(translationInteractor.getOnSourceSubject()
                .subscribe(translation -> view.get().showSource(translation.getSourceLanguage())));

        subscriptions.add(translationInteractor.getOnTargetSubject()
                .subscribe(translation -> view.get().showTarget(translation.getTargetLanguage())));
    }


    @Override
    public void detach() {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        view.clear();
    }

    @Override
    public void onInputChanged(String text) {
        subscriptions.add(translationInteractor.onInputChanged(text)
                .subscribe(
                        translation -> {},
                        throwable -> System.out.println(throwable.toString())));
    }

    @Override
    public void loadTranslation() {
        subscriptions.add(translationInteractor.getLastTranslation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        translation -> {
                            view.get().showSource(translation.getSourceLanguage());
                            view.get().showTarget(translation.getTargetLanguage());
                            view.get().showInput(translation.getInput());
                            view.get().showOutput(translation.getOutputWithWatermark());
                        },
                        throwable -> view.get().showError(throwable))
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




}
