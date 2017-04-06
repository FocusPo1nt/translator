package com.focuspoint.translator.di.modules;

import android.app.Application;

import com.focuspoint.translator.App;
import com.focuspoint.translator.interactors.LanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.presenters.LanguageScreenPresenter;
import com.focuspoint.translator.screen.LanguageScreenContract;

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

    //endregion

    //region Presenters
    @Provides
    @Singleton
    LanguageScreenContract.Presenter providePresenter (ILanguageInteractor interactor) {
        return new LanguageScreenPresenter(interactor);
    }



    //endregion
}