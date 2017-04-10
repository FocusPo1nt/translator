package com.focuspoint.translator.interactors.interfaces;

import com.focuspoint.translator.models.Language;

import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Interactor for dealing with Language class data;
 */

public interface ILanguageInteractor {

    Observable <Map<String, Language>> loadLanguages();

}
