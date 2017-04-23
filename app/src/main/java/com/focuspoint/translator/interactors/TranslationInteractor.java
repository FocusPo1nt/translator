package com.focuspoint.translator.interactors;

import com.focuspoint.translator.database.DB;
import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Model;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.network.DictionaryApiService;
import com.focuspoint.translator.network.TranslateApiService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;

import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Interactor implementation;
 */


public class TranslationInteractor implements ITranslationInteractor {

    private ILanguageInteractor languageInteractor;
    private TranslateApiService translateApiService;
    private DictionaryApiService dictionaryApiService;
    private Model model;
    private DB database;
    private boolean translating;

    @Inject
    public TranslationInteractor(ILanguageInteractor languageInteractor, TranslateApiService apiService, Model model, DB database, DictionaryApiService dictionaryApiService) {
        this.languageInteractor = languageInteractor;
        this.translateApiService = apiService;
        this.dictionaryApiService = dictionaryApiService;
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
                    saveDB(result);
                    translationSubject.onNext(result);
                })
                .doOnEach(t -> translating = false);

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
        saveDB(translation);

        if (model.getCurrentTranslation()!=null){
            if (model.getCurrentTranslation().equals(translation))
            favoriteSubject.onNext(translation);
        }
    }

    @Override
    public Observable <Translation> addCurrentToHistory() {
        return  getLastTranslation()
                .filter(translation -> !translation.getInput().trim().isEmpty())
                .filter(translation -> !translation.getOutput().trim().isEmpty())
                .delay(500, TimeUnit.MILLISECONDS)
                .doOnNext(translation -> translation.setFavorite(translation.isFavorite()))
                .doOnNext(this::saveDB);
    }

    @Override
    public Observable<Boolean> changeCurrentFavorite() {
        if (translating) return Observable.error(
                new IllegalStateException("Cant add to favorite when translating"));

        return getLastTranslation()
                .filter(translation -> !translation.getInput().isEmpty())
                .doOnNext(translation -> translation.setFavorite(!translation.isFavorite()))
                .doOnNext(this::saveDB)
                .map(Translation::isFavorite);
    }


    @Override
    public void clearCurrent() {
        model.setCurrentInput("");
        model.setCurrentOutput("");
        model.setDictionary("");
        model.setFavorite(false);
        translationSubject.onNext(model.getCurrentTranslation());
    }


    @Override
    public void setCurrent(Translation translation) {
        translation.setDate(System.currentTimeMillis());
        saveDB(translation);
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

        return dictionaryApiService.lookup(translation.getInput(), translation.getDirection())
//                .doOnNext(r -> translation.setDictionary(r.toString())
                .doOnNext(dictionaryRM -> translation.setDictionary(dictionaryRM.toString()))
                .flatMap(r -> translateApiService
                        .translate(translation.getInput(), translation.getDirection()))


//
//        return translateApiService
//                .translate(translation.getInput(), translation.getDirection())
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

    private void  saveDB(Translation translation){
        if (translation.getOutput() != null
                && translation.getInput() != null
                && !translation.getOutput().isEmpty()
                && !translation.getInput().isEmpty()){
            database.save(translation);
        }
    }
}
