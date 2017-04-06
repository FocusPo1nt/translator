package com.focuspoint.translator.presenters;

import com.focuspoint.translator.interactors.LanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.screen.LanguageScreenContract;

/**
 * Created by root on 06.04.17.
 */

public class LanguageScreenPresenter implements LanguageScreenContract.Presenter {

    private ILanguageInteractor interactor;

    public LanguageScreenPresenter(ILanguageInteractor interactor){
        this.interactor = interactor;
    }

    @Override
    public void attach(LanguageScreenContract.View view) {

    }

    @Override
    public void detach() {

    }

    @Override
    public void loadLanguages() {

    }
}
