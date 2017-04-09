package com.focuspoint.translator.interactors.interfaces;

import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.models.responseModels.TranslationRM;
import com.focuspoint.translator.network.TranslateApiService;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Interactor implementation;
 * handles current Translation state;
 */

public class TranslationInteractor implements ITranslationInteractor {

    private ILanguageInteractor languageInteractor;
    private Retrofit retrofit;


    @Inject
    public TranslationInteractor(ILanguageInteractor languageInteractor,  Retrofit retrofit){
        this.languageInteractor = languageInteractor;
        this.retrofit = retrofit;
    }

    @Override
    public Observable <Translation> getLastTranslation() {
        return languageInteractor.loadLanguages()
                .flatMap((map) -> Observable.just(new Translation(map.get("en"), map.get("ru"))));
    }



    @Override
    public Observable<Translation> translate(Translation translation) {

        return retrofit.create(TranslateApiService.class)
                .translate(translation.getInput(), translation.getDirection())
                .subscribeOn(Schedulers.io())
                .map(translationRM -> {
                    translation.setOutput(translationRM.text.get(0));
                    return translation;
                });


    }
}
