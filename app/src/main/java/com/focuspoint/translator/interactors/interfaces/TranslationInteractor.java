package com.focuspoint.translator.interactors.interfaces;

import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Translation;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Interactor implementation;
 * handles current Translation state;
 */

public class TranslationInteractor implements ITranslationInteractor {
    private Translation currentTranslation;
    private ILanguageInteractor languageInteractor;


    @Inject
    public TranslationInteractor(ILanguageInteractor languageInteractor){
        this.languageInteractor = languageInteractor;
    }

    @Override
    public Observable <Translation> getCurrentTranslation() {
        if (currentTranslation != null){
            return Observable.just(currentTranslation);
        }else{
            return languageInteractor.loadLanguages()
                    .flatMap((map) -> Observable.just(new Translation(map.get("en"), map.get("ru"))))
                    .doOnNext(translation -> this.currentTranslation = translation);
        }
    }

    @Override
    public void setCurrentTranslation(Translation translation) {
        this.currentTranslation = translation;
    }
}
