package com.focuspoint.translator.di.modules;

import android.app.Application;

import com.focuspoint.translator.App;
import com.focuspoint.translator.database.DB;
import com.focuspoint.translator.interactors.LanguageInteractor;
import com.focuspoint.translator.interactors.TranslationInteractor;
import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Model;
import com.focuspoint.translator.network.TranslateApiService;
import com.focuspoint.translator.presenters.FavoritePresenter;
import com.focuspoint.translator.presenters.HistoryPresenter;
import com.focuspoint.translator.presenters.LanguagePresenter;
import com.focuspoint.translator.presenters.TranslationPresenter;
import com.focuspoint.translator.screen.FavoriteScreenContract;
import com.focuspoint.translator.screen.TranslationListContract;
import com.focuspoint.translator.screen.LanguageScreenContract;
import com.focuspoint.translator.screen.TranslationScreenContract;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
    ILanguageInteractor provideLanguageInteractor(TranslateApiService apiService, DB database) {
        return new LanguageInteractor(apiService, database);
    }

    @Provides
    @Singleton
    ITranslationInteractor provideTranslationInteractor(ILanguageInteractor interactor, TranslateApiService apiService, Model model, DB db) {
        return new TranslationInteractor( interactor, apiService, model, db);
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
    TranslationScreenContract.Presenter provideTranslationPresenter (ITranslationInteractor interactor, ILanguageInteractor languageInteractor) {
        return new TranslationPresenter(interactor, languageInteractor);
    }



    @Provides
    @Singleton
    TranslationListContract.HistoryPresenter provideHistoryPresenter (ITranslationInteractor interactor) {
        return new HistoryPresenter(interactor);
    }

    @Provides
    @Singleton
    TranslationListContract.FavoritePresenter provideFavoritePresenter (ITranslationInteractor interactor) {
        return new FavoritePresenter(interactor);
    }

    //endregion
}