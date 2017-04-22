package com.focuspoint.translator.interactors;

import com.focuspoint.translator.database.DB;
import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Model;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.network.TranslateApiService;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Interactor implementation;
 */


public class TranslationInteractor implements ITranslationInteractor {

    private ILanguageInteractor languageInteractor;
    private TranslateApiService apiService;
    private Model model;
    private DB database;
    private boolean translating;

    @Inject
    public TranslationInteractor(ILanguageInteractor languageInteractor, TranslateApiService apiService, Model model, DB database) {
        System.out.println("CONSTRUCTOR " + hashCode());
        this.languageInteractor = languageInteractor;
        this.apiService = apiService;
        this.model = model;
        this.database = database;
    }

    @Override
    public Observable<Translation> getLastTranslation() {

        return Observable
                .concat(
                        Observable.just(model.getCurrentTranslation()),
                        getLastFromDB(),
                        Observable.just(Translation.obtainDefault()))
                .first(translation -> translation != null)
                .doOnNext(result -> model.setCurrentTranslation(result));
    }


   /**Translate from DB if exist
    * Translate from Network
    * Fill raw Translation with Language objects
    * */
    @Override
    public Observable<Translation> translate(Translation translation) {
        translating = true;

        translation.prepareForSQLstorage();
        System.out.println(translation.getInput());
        Observable<Translation> rawTranslationObserver =  Observable
                .concat(
                        translateFromDB(translation),
                        translateFromApi(translation))
                .first(raw -> raw != null);


        return Observable.combineLatest(
                rawTranslationObserver, languageInteractor.loadLanguages(), (result, map) ->{
                    if (result != null) {
                        result.setSourceLanguage(map.get(result.getSource()));
                        result.setTargetLanguage(map.get(result.getTarget()));
                    }
                    return result;
                })
                .doOnNext(result -> {
                    result.setDate(System.currentTimeMillis());
                    model.setCurrentTranslation(result);
                    database.saveDB(result);
                    translating = false;
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
    public Observable<Translation> changeCurrentLanguage(Language source, Language target){
        return getLastTranslation()
                .flatMap(t -> {
                    // check if need reverse;
                    if ((t.getSourceLanguage().equals(target) || t.getTargetLanguage().equals(source))
                            && !t.getSource().equals(t.getTarget())){
                        return reverseLanguages();
                    }else{
                        if (source!=null){
                            return onSourceChanged(source);
                        }else{
                            return onTargetChanged(target);
                        }
                    }
                });
    }




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
                .flatMap(this::translate)
                .flatMap(translation -> addCurrentToHistory());
    }




    @Override
    public Observable<List<Translation>> getHistory() {

        return Observable.combineLatest(database.getHistory(), languageInteractor.loadLanguages(), (list, map) -> {

            for (Translation translation: list){
                translation.setSourceLanguage(map.get(translation.getSource()));
                translation.setTargetLanguage(map.get(translation.getTarget()));
            }
            return list;
        });
    }

    @Override
    public Observable<List<Translation>> getFavorites() {
        return Observable.combineLatest(database.getFavorites(), languageInteractor.loadLanguages(), (list, map) -> {

            for (Translation translation: list){
                translation.setSourceLanguage(map.get(translation.getSource()));
                translation.setTargetLanguage(map.get(translation.getTarget()));
            }
            return list;
        });
    }

    @Override
    public void setFavorite(Translation translation, boolean favorite) {
        if (translating) return;

        translation.setFavorite(favorite);
        database.saveDB(translation);

        if (model.getCurrentTranslation()!=null){
            if (model.getCurrentTranslation().equals(translation))
            favoriteSubject.onNext(translation);
        }
    }

    @Override
    public Observable <Translation> addCurrentToHistory() {
        if (translating) return Observable.error(
                new IllegalStateException("Cant add to history when translating"));

        return getLastTranslation()
                .filter(translation -> !translation.getInput().isEmpty())
                .doOnNext(translation -> translation.setFavorite(translation.isFavorite()))
                .doOnNext(translation ->  database.saveDB(translation));
    }

    @Override
    public Observable<Boolean> changeCurrentFavorite() {
        if (translating) return Observable.error(
                new IllegalStateException("Cant add to favorite when translating"));

        return getLastTranslation()
                .filter(translation -> !translation.getInput().isEmpty())
                .doOnNext(translation -> translation.setFavorite(!translation.isFavorite()))
                .doOnNext(translation ->  database.saveDB(translation))
                .map(Translation::isFavorite);
    }


    @Override
    public void clearCurrent() {
        model.setCurrentInput("");
        model.setCurrentOutput("");
        model.setFavorite(false);
        translationSubject.onNext(model.getCurrentTranslation());
    }


    @Override
    public void setCurrent(Translation translation) {
        translation.setDate(System.currentTimeMillis());
        database.saveDB(translation);
        model.setCurrentTranslation(translation);

        translationSubject.onNext(model.getCurrentTranslation());
    }


    @Override
    public void clearFavorites() {
        database.clearFavorites();
    }

    @Override
    public void clearHistory() {
        database.clearHistory();
    }


    private Observable<Translation> translateFromDB(Translation translation) {
        return database.translate(translation)
                .first();
    }

    private Observable<Translation> translateFromApi(Translation translation){
        return apiService
                .translate(translation.getInput(), translation.getDirection())
                .subscribeOn(Schedulers.io())
                .map(translationRM -> translation
                        .setOutput(translationRM.text.get(0))
                        .setStorage(Translation.STORAGE_DEFAULT));
    }


    private Observable<Translation> getLastFromDB(){

        return Observable.combineLatest(
                database.getLastTranslation().first(),
                languageInteractor.loadLanguages(), (result, map) ->{
                    if (result != null) {
                        result.setSourceLanguage(map.get(result.getSource()));
                        result.setTargetLanguage(map.get(result.getTarget()));
                    }
                    return result;
                })
                .first();

    }


    private Observable<Translation> onSourceChanged(Language language) {
        return getLastTranslation()
                .doOnNext(translation -> translation.setSourceLanguage(language))
                .doOnNext(translation -> sourceSubject.onNext(translation))
                .flatMap(this::translate)
                .flatMap(translation -> addCurrentToHistory());
    }

    private Observable<Translation> onTargetChanged(Language language) {
        return getLastTranslation()
                .doOnNext(translation -> translation.setTargetLanguage(language))
                .doOnNext(translation -> targetSubject.onNext(translation))
                .flatMap(this::translate)
                .flatMap(translation -> addCurrentToHistory());
    }

    private PublishSubject<Translation> translationSubject = PublishSubject.create();

    private PublishSubject<Translation> sourceSubject = PublishSubject.create();

    private PublishSubject<Translation> targetSubject = PublishSubject.create();

    private PublishSubject<Translation> favoriteSubject = PublishSubject.create();
}
