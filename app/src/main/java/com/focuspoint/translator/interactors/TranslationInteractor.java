package com.focuspoint.translator.interactors;

import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Model;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.models.responseModels.TranslationRM;
import com.focuspoint.translator.network.TranslateApiService;
import com.focuspoint.translator.screen.fragment.TranslateFragment;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.Observable;

import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Interactor implementation;
 * handles current Translation state;
 */

public class TranslationInteractor implements ITranslationInteractor {

    private ILanguageInteractor languageInteractor;
    private Retrofit retrofit;
    private Model model;


    @Inject
    public TranslationInteractor(ILanguageInteractor languageInteractor, Retrofit retrofit, Model model) {
        this.languageInteractor = languageInteractor;
        this.retrofit = retrofit;
        this.model = model;
    }

    @Override
    public Observable<Translation> getLastTranslation() {

        if (model.getCurrentTranslation() != null) {
            return Observable.just(model.getCurrentTranslation());
        } else {
            return languageInteractor.loadLanguages()
                    .flatMap((map) -> Observable.just(new Translation(map.get("en"), map.get("ru"))))
                    .doOnNext(translation -> {
                        model.setCurrentTranslation(translation);
                    });
        }


    }


    @Override
    public Observable<Translation> translate(Translation translation) {
        System.out.println("TRANSLATE ON "  + this.toString());

        return retrofit.create(TranslateApiService.class)
                .translate(translation.getInput(), translation.getDirection())
                .subscribeOn(Schedulers.io())
                .map(translationRM -> translation.setOutput(translationRM.text.get(0)))
                .doOnNext(result -> translationSubject.onNext(result));
    }


    @Override
    public Observable<Translation> onInputChanged(String input) {

        return getLastTranslation()
                .filter(translation -> input.length() > 0)
                .doOnNext(translation -> translation.setInput(input))
                .flatMap(this::translate);
    }

    @Override
    public Observable<Translation> onSourceChanged(Language language) {
        return getLastTranslation()
                .doOnNext(translation -> translation.setSourceLanguage(language))
                .doOnNext(translation -> sourceSubject.onNext(translation))
                .flatMap(this::translate);
    }

    @Override
    public Observable<Translation> onTargetChanged(Language language) {
        return getLastTranslation()
                .doOnNext(translation -> translation.setTargetLanguage(language))
                .doOnNext(translation -> targetSubject.onNext(translation))
                .flatMap(this::translate);
    }

    private PublishSubject<Translation> translationSubject = PublishSubject.create();

    private PublishSubject<Translation> sourceSubject = PublishSubject.create();

    private PublishSubject<Translation> targetSubject = PublishSubject.create();


    @Override
    public Observable<Translation> getOnTranslateSubject() {
        System.out.println("GET SUBJECT " + translationSubject.toString());
        return translationSubject;
    }

    @Override
    public PublishSubject<Translation> getOnSourceSubject() {
        return sourceSubject;
    }

    @Override
    public PublishSubject<Translation> getOnTargetSubject() {
        return targetSubject;
    }
}
