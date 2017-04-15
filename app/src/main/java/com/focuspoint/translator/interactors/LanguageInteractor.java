package com.focuspoint.translator.interactors;

import com.focuspoint.translator.database.DB;
import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.models.responseModels.LanguagesRM;
import com.focuspoint.translator.network.TranslateApiService;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;


/**
 * Interactor implementation dealing with Language objects;
 */

public class LanguageInteractor implements ILanguageInteractor {
    private Retrofit retrofit;
    private Map<String, Language> languageMap;
    private DB database;


    @Inject
    public LanguageInteractor(Retrofit retrofit, DB database){
        this.retrofit = retrofit;
        this.database = database;

    }

    @Override
    public Observable<Map<String, Language>> loadLanguages() {

        return Observable.concat(
                Observable.just(languageMap),
                database.getLanguages().first().doOnNext(map -> languageMap = map),
                loadFromApi()
        )
                .first(map -> map!= null && !map.isEmpty());
    }

    private Observable<Map<String, Language>> loadFromApi(){
        return retrofit.create(TranslateApiService.class)
                .getLangs("ru") //TODO add user language
                .map(LanguagesRM::obtainLanguages)
                .doOnNext(map -> database.saveDB(new ArrayList<>(map.values())))
                .doOnNext(map -> this.languageMap = map);
    }


    private PublishSubject<Language> sourceSubject =  PublishSubject.create();


    private PublishSubject<Language> targetSubject =  PublishSubject.create();

}