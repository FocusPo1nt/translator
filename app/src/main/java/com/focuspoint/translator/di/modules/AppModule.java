package com.focuspoint.translator.di.modules;

import android.app.Application;

import com.focuspoint.translator.App;
import com.focuspoint.translator.interactors.LanguageInteractor;
import com.focuspoint.translator.interactors.TranslationInteractor;
import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Model;
import com.focuspoint.translator.presenters.LanguagePresenter;
import com.focuspoint.translator.presenters.TranslationPresenter;
import com.focuspoint.translator.screen.LanguageScreenContract;
import com.focuspoint.translator.screen.TranslationScreenContract;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by root on 30.03.17.
 */

@Module
public class AppModule {
    App application;

    public AppModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }


    //region Interactors
    @Provides
    @Singleton
    ILanguageInteractor provideLanguageInteractor(Retrofit retrofit) {
        return new LanguageInteractor(retrofit);
    }

    @Provides
    @Singleton
    ITranslationInteractor provideTranslationInteractor(ILanguageInteractor interactor, Retrofit retrofit, Model model) {
        return new TranslationInteractor( interactor, retrofit, model);
    }

    //endregion

    //region Presenters
    @Provides
    @Singleton
    LanguageScreenContract.Presenter provideLanguagePresenter (ILanguageInteractor interactor, ITranslationInteractor translationInteractor) {
        return new LanguagePresenter(interactor, translationInteractor);
    }


    @Provides
    @Singleton
    TranslationScreenContract.Presenter provideTranslationPresenter (TranslationInteractor interactor, LanguageInteractor languageInteractor) {
        return new TranslationPresenter(interactor, languageInteractor);
    }


    //endregion
}