package com.focuspoint.translator.di.modules;

import android.app.Application;

import com.focuspoint.translator.App;

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
}