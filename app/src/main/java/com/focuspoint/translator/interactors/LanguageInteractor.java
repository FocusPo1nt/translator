package com.focuspoint.translator.interactors;

import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.responseModels.LanguagesRM;
import com.focuspoint.translator.network.TranslateApiService;

import java.util.Map;

import retrofit2.Retrofit;
import rx.Observable;
import rx.functions.Func1;

/**
 * Interactor implementation dealing with Language objects;
 */

public class LanguageInteractor implements ILanguageInteractor {
    private Retrofit retrofit;

    public LanguageInteractor(Retrofit retrofit){
        this.retrofit = retrofit;
    }

    @Override
    public Observable<Map<String, Language>> loadLanguages() {

        return retrofit.create(TranslateApiService.class)
                .getLangs("ru") //TODO add user language
                .map(LanguagesRM::obtainLanguages);
    }
}
