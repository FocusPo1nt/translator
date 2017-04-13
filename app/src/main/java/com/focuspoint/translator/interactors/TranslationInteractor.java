package com.focuspoint.translator.interactors;

import com.focuspoint.translator.database.DB;
import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Model;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.models.responseModels.TranslationRM;
import com.focuspoint.translator.network.TranslateApiService;
import com.focuspoint.translator.screen.fragment.TranslateFragment;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.Observable;

import rx.functions.Action1;
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
    private DB database;


    @Inject
    public TranslationInteractor(ILanguageInteractor languageInteractor, Retrofit retrofit, Model model, DB database) {
        this.languageInteractor = languageInteractor;
        this.retrofit = retrofit;
        this.model = model;
        this.database = database;
    }

    @Override
    public Observable<Translation> getLastTranslation() {

        if (model.getCurrentTranslation() != null) {
            return Observable.just(model.getCurrentTranslation());
        } else {
            return languageInteractor.loadLanguages()
                    .flatMap((map) -> Observable.just(new Translation(
                            map.get("en"),
                            map.get("ru"),
                            Translation.DEFAULT_INPUT,
                            Translation.DEFAULT_OUTPUT)))
                    .doOnNext(translation -> model.setCurrentTranslation(translation));
        }
    }


    @Override
    public Observable<Translation> translate(Translation translation) {

        database.getTranslations()
                .doOnNext(translations -> System.out.println("sd;lkjf")).subscribe(new Action1<List<Translation>>() {
            @Override
            public void call(List<Translation> translations) {
                System.out.println("sdfoiasjdf");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                System.out.println(throwable.toString());
            }
        })

        ;





        return retrofit.create(TranslateApiService.class)
                .translate(translation.getInput(), translation.getDirection())
                .subscribeOn(Schedulers.io())
                .map(translationRM -> translation.setOutput(translationRM.text.get(0)))
                .doOnNext(result -> {
                    result.setDate(System.currentTimeMillis());
                    database.saveDB(translation);//TODO
                    translationSubject.onNext(result);
                });
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

    @Override
    public Observable<Translation> revereLanguages() {
        return getLastTranslation()
                .doOnNext(Translation::reverseLanguages)
                .doOnNext(translation -> targetSubject.onNext(translation))
                .doOnNext(translation -> sourceSubject.onNext(translation))
                .flatMap(this::translate);
    }
}
