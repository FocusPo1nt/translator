package com.focuspoint.translator.interactors.interfaces;

import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Translation;

import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.subjects.PublishSubject;

/**
 * Deals with Translation data;
 */

public interface ITranslationInteractor {

    Observable <Translation> getLastTranslation();

    Observable <Translation> translate(Translation translation);

    Observable <Translation> onInputChanged(String input);

    Observable <Translation> onSourceChanged(Language language);

    Observable <Translation> onTargetChanged (Language language);

    Observable <Translation> getOnTranslateSubject();

    PublishSubject <Translation> getOnSourceSubject();

    PublishSubject <Translation> getOnTargetSubject();

    Observable <Translation> revereLanguages();
}
