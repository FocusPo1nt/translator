package com.focuspoint.translator.interactors;

import com.focuspoint.translator.database.DB;
import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.responseModels.LanguagesRM;
import com.focuspoint.translator.network.TranslateApiService;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;



/**
 * Interactor implementation dealing with Language objects;
 */

public class LanguageInteractor implements ILanguageInteractor {
    private TranslateApiService apiService;
    private DB database;
    private Map<String, Language> languageMap;
    private Map<String, Language> defaultMap;


    @Inject
    public LanguageInteractor(TranslateApiService apiService, DB database){
        this.apiService = apiService;
        this.database = database;
        defaultMap = Language.obtainDefaultMap();

    }


    /**If in memory -> return from memory  else
     * If in database -> return from database and cache memory else
     * If in network -> return form network and cache in memory and database
     * If network error -> return default Map of languages without caching;
     * */
    @Override
    public Observable<Map<String, Language>> loadLanguages() {
        return Observable.concat(
                Observable.just(languageMap),
                database.getLanguages().first().doOnNext(map -> languageMap = map),
                loadFromApi().onErrorResumeNext(throwable -> Observable.just(defaultMap)))
                .first(map -> map!= null && !map.isEmpty());
    }

    private Observable<Map<String, Language>> loadFromApi(){
        return apiService
                .getLangs(Locale.getDefault().getLanguage()) //TODO check for user change language
                .map(LanguagesRM::obtainLanguages)
                .doOnNext(map -> {
                    database.saveDB(new ArrayList<>(map.values())); //Кешируем список языков в хипе и базе
                    this.languageMap = map;
                });

    }
}