package com.focuspoint.translator.interactors.interfaces;

import com.focuspoint.translator.models.Language;

import java.util.Map;

import rx.Observable;

/**
 * Interactor for dealing with Language class data;
 */

public interface ILanguageInteractor {

    Observable <Map<String, Language>> loadLanguages();

}
