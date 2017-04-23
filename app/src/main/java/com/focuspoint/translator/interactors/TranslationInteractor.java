package com.focuspoint.translator.interactors;

import com.focuspoint.translator.database.DB;
import com.focuspoint.translator.interactors.interfaces.IErrorInteractor;
import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Model;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.network.DictionaryApiService;
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
    private TranslateApiService translateApiService;
    private DictionaryApiService dictionaryApiService;
    private IErrorInteractor errorInteractor;
    private Model model;
    private DB database;
    private boolean translating; //флаг который говорит, что идет перевод;

    @Inject
    public TranslationInteractor
            (ILanguageInteractor languageInteractor, TranslateApiService apiService, Model model,
             DB database, DictionaryApiService dictionaryApiService, IErrorInteractor errorInteractor) {
        this.languageInteractor = languageInteractor;
        this.translateApiService = apiService;
        this.dictionaryApiService = dictionaryApiService;
        this.model = model;
        this.database = database;
        this.errorInteractor = errorInteractor;
    }



    @Override
    public Observable<Translation> getLastTranslation() {

        //если в памяти есть перевод возвращаем его
        //иначе смотрим базу на последнего по дате
        //иначе берем дефолтного;


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
    * Fill raw Translation with Language objects;
    * */
    @Override
    public Observable<Translation> translate(Translation translation) {

        //Основной метод на котором происходит заполнение поля output у Translation;


        translating = true;

        translation.prepareForSQLstorage(); // сразу удаляем спецсимволы;

        Observable<Translation> rawTranslationObserver =  Observable // тут собственно переводим;
                .concat(
                        translateFromDB(translation), // если есть то из базы;
                        translateFromApi(translation)) // если нет то с сервера;
                .first(raw -> raw != null);



        // далее заполняем перевод обьектами языка (с описанием и кодом языка);
        return Observable.combineLatest(
                rawTranslationObserver, languageInteractor.loadLanguages(), (result, map) ->{
                    if (result != null) {
                        result.setSourceLanguage(map.get(result.getSource()));
                        result.setTargetLanguage(map.get(result.getTarget()));
                    }
                    return result;
                })
                .doOnError(e -> errorInteractor.onError(e)) // если была ошибка передаем в глобальный перехватчик;
                .doOnNext(result -> {
                    result.setDate(System.currentTimeMillis());
                    model.setCurrentTranslation(result);
                    saveDB(result);
                    translationSubject.onNext(result); // рассылаем результат всем подписчикам;
                })
                .doOnEach(t -> translating = false); // при любом раскладе сбрасываем флаг;

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

        //Проверяем может нужно поменять местами языки
        //Если нет вызываем одноименные методы

        return getLastTranslation()
                .flatMap(t -> {

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
    public Observable<List<Translation>> getHistory() {
        //Берем из базы и заполняем обьектами Language;


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
                .doOnNext(translation -> translation.setFavorite(translation.isFavorite())) // получает статус в истории либо избранный;
                .doOnNext(this::saveDB);
    }

    @Override
    public Observable<Boolean> changeCurrentFavorite() {

        // пока идет перевод - текущий перевод нет смысла добавлять в избранное;
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
                .subscribeOn(Schedulers.io())
                .map(dictionaryRM -> translation.setDictionary(dictionaryRM.toString())) // Парсим ответ;
                .onErrorReturn(throwable -> translation.setDictionary(null)) // Игнорируем ошибки словаря;
                .flatMap(t -> translateApiService.translate(t.getInput(), t.getDirection())) //Идем за переводом;
                .map(translationRM -> translation // парсим;
                        .setOutput(translationRM.text.get(0)) // с сервера всегда возвращается одно значение;
                        .setStorage(Translation.STORAGE_DEFAULT)); // указываем стандартный статус. Он не показывается в истории и избранном;
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
                .flatMap(this::translate) // переводим;
                .flatMap(translation -> addCurrentToHistory()); // добавляем в историю;
    }

    private Observable<Translation> onTargetChanged(Language language) {
        return getLastTranslation()
                .doOnNext(translation -> translation.setTargetLanguage(language))
                .doOnNext(translation -> targetSubject.onNext(translation))
                .flatMap(this::translate) // переводим;
                .flatMap(translation -> addCurrentToHistory()); // в историю;
    }


    @Override
    public Observable<Translation> reverseLanguages() {
        return getLastTranslation()
                .doOnNext(Translation::reverseLanguages)
                .doOnNext(translation -> model.setCurrentTranslation(translation))
                .doOnNext(translation -> targetSubject.onNext(translation))
                .doOnNext(translation -> sourceSubject.onNext(translation))
                .flatMap(this::translate) // переводим;
                .flatMap(translation -> addCurrentToHistory()); // в историю;
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
