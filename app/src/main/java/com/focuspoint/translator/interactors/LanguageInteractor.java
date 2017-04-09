package com.focuspoint.translator.interactors;

import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.responseModels.LanguagesRM;
import com.focuspoint.translator.network.TranslateApiService;

import java.util.Map;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;


/**
 * Interactor implementation dealing with Language objects;
 */

public class LanguageInteractor implements ILanguageInteractor {
    private Retrofit retrofit;
    private Map<String, Language> languageMap;

    @Inject
    public LanguageInteractor(Retrofit retrofit){
        this.retrofit = retrofit;
    }

    @Override
    public Observable<Map<String, Language>> loadLanguages() {
        if (languageMap != null){
            return Observable.just(languageMap);
        }else {
            return retrofit.create(TranslateApiService.class)
                    .getLangs("ru") //TODO add user language
                    .map(LanguagesRM::obtainLanguages)
                    .doOnNext(map -> this.languageMap = map);
        }
    }


    @Override
    public void changeSource(Language source) {
        sourceSubject.onNext(source);
    }

    @Override
    public void changeTarget(Language target) {
        targetSubject.onNext(target);
    }

    @Override
    public Observable<Language> getSourceSubject() {
        return sourceSubject;
    }

    @Override
    public Observable<Language> getTargetSubject() {
        return targetSubject;
    }



    private PublishSubject<Language> sourceSubject =  PublishSubject.create();


    private PublishSubject<Language> targetSubject =  PublishSubject.create();
}