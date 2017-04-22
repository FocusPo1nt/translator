package com.focuspoint.translator.interactors.interfaces;

import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Translation;

import java.util.List;

import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.subjects.PublishSubject;

/**
 * Deals with Translation data;
 */

public interface ITranslationInteractor {

    Observable <Translation> getLastTranslation();

    Observable <List<Translation>> getHistory();

    Observable <List<Translation>> getFavorites();

    Observable <Translation> translate(Translation translation);

    Observable <Translation> onInputChanged(String input);

    Observable <Translation> changeCurrentLanguage (Language source, Language target);

    Observable <Translation> getOnTranslateSubject();

    PublishSubject <Translation> getOnSourceSubject();

    PublishSubject <Translation> getOnTargetSubject();

    PublishSubject <Translation> getOnFavoriteSubject();

    Observable <Translation> reverseLanguages();

    Observable <Translation> addCurrentToHistory();

    void setFavorite(Translation translation, boolean favorite);

    Observable<Boolean> changeCurrentFavorite();

    void clearCurrent();

    void setCurrent(Translation translation);

    void clearFavorites ();

    void clearHistory();
}
