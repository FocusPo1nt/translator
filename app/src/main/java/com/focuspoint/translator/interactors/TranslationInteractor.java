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
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Retrofit;
import rx.Observable;

import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
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
        System.out.println("CONSTRUCTOR " + hashCode());
        this.languageInteractor = languageInteractor;
        this.retrofit = retrofit;
        this.model = model;
        this.database = database;
    }

    @Override
    public Observable<Translation> getLastTranslation() {

        return Observable.concat(
                Observable.just(model.getCurrentTranslation()),
                getLastFromDB(),
                Observable.just(Translation.obtainDefault())
                        .doOnNext(t -> model.setCurrentTranslation(t))
        ).first(translation -> translation != null)
                .doOnNext(translation -> System.out.println("FINAL LAST TRANSLATION = " +translation));

    }


   /**Translate from DB if exist
    * Translate from Network
    * Fill raw Translation with Language objects
    * */
    @Override
    public Observable<Translation> translate(Translation translation) {
        System.out.println("to translate " + translation);

        Observable<Translation> rawTranslationObserver =  Observable.concat(
                translateFromDB(translation),
                translateFromApi(translation)) //if network error -> doesn't get from DB
                .first(raw -> raw != null);


        return Observable.combineLatest(
                rawTranslationObserver, languageInteractor.loadLanguages(), (result, map) ->{
                    result.setSourceLanguage(map.get(result.getSource()));
                    result.setTargetLanguage(map.get(result.getTarget()));
                    return result;
                })
                .doOnNext(result -> {
                    result.setDate(System.currentTimeMillis());
                    model.setCurrentTranslation(result);
                    database.saveDB(result);
                    translationSubject.onNext(result);
                    System.out.println("----------------");
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

    private PublishSubject<Translation> favoriteSubject = PublishSubject.create();


    @Override
    public Observable<Translation> getOnTranslateSubject() {
        System.out.println("FROM " + this.hashCode());
        System.out.println("GET TRANSLATION SUBJECT " + targetSubject.hashCode());
        Observable<Translation> observable = translationSubject;//.doOnNext(translation -> System.out.println("DOONEXT TRANSLATION " + translationSubject.hashCode()));
        System.out.println("GET TRANSLATION OBSERVABLE " + observable.hashCode());
        return observable;
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
    public PublishSubject<Translation> getOnFavoriteSubject() {
        return favoriteSubject;
    }

    @Override
    public Observable<Translation> reverseLanguages() {
        return getLastTranslation()
                .doOnNext(Translation::reverseLanguages)
                .doOnNext(translation -> model.setCurrentTranslation(translation))
                .doOnNext(translation -> targetSubject.onNext(translation))
                .doOnNext(translation -> sourceSubject.onNext(translation))
                .flatMap(this::translate);
    }

    private Observable<Translation> translateFromDB(Translation translation) {
        return database.translate(translation)
                .first();
    }

    private Observable<Translation> translateFromApi(Translation translation){
        return retrofit.create(TranslateApiService.class)
                .translate(translation.getInput(), translation.getDirection())
                .subscribeOn(Schedulers.io())
                .map(translationRM -> translation
                        .setOutput(translationRM.text.get(0))
                        .setFavorite(false));
    }


    private Observable<Translation> getLastFromDB(){

        return Observable.combineLatest(
                database.getLastTranslation().first(),
                languageInteractor.loadLanguages(), (result, map) ->{
                    result.setSourceLanguage(map.get(result.getSource()));
                    result.setTargetLanguage(map.get(result.getTarget()));
                    return result;
                })     .doOnNext(translation -> System.out.println("LAST DB IN INTERACTOR " + translation
        ))
                .doOnNext(translation -> model.setCurrentTranslation(translation));

    }


    @Override
    public Observable<List<Translation>> getHistory() {
        return database.getTranslations()
                .withLatestFrom(languageInteractor.loadLanguages(), (list, map) -> {

                    for (Translation translation: list){
                        translation.setSourceLanguage(map.get(translation.getSource()));
                        translation.setTargetLanguage(map.get(translation.getTarget()));
                    }
                    return list;
                });
//                .first();
    }

    @Override
    public Observable<List<Translation>> getFavorites() {
        return database.getFavorites()
                .withLatestFrom(languageInteractor.loadLanguages(), (list, map) -> {
                    for (Translation translation: list){
                        translation.setSourceLanguage(map.get(translation.getSource()));
                        translation.setTargetLanguage(map.get(translation.getTarget()));
                    }
                    return list;
                });
//                .first();
    }

    @Override
    public void setFavorite(Translation translation, boolean favorite) {

        translation.setFavorite(favorite);
        database.saveDB(translation);

        if (model.getCurrentTranslation()!=null){
            if (model.getCurrentTranslation().equals(translation))
            favoriteSubject.onNext(translation);
        }
    }


    @Override
    public Observable<Boolean> changeCurrentFavorite() {
        return getLastTranslation()
                .doOnNext(translation -> translation.setFavorite(!translation.isFavorite()))
                .doOnNext(translation -> database.saveDB(translation))
                .map(Translation::isFavorite);
    }
}
