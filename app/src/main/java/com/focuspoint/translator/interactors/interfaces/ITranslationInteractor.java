package com.focuspoint.translator.interactors.interfaces;

import com.focuspoint.translator.models.Translation;

import rx.Observable;

/**
 * Deals with Translation data;
 */

public interface ITranslationInteractor {

    Observable <Translation> getCurrentTranslation();

    void setCurrentTranslation(Translation translation);

}
