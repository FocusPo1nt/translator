package com.focuspoint.translator.di.components;

import com.focuspoint.translator.screen.activity.MainActivity;
import com.focuspoint.translator.di.modules.DataModule;
import com.focuspoint.translator.di.modules.AppModule;

import javax.inject.Singleton;
import dagger.Component;

/**
 * Created by v_banko on 3/29/2017.
 */
@Singleton
@Component(modules = {
        AppModule.class,
        DataModule.class
//        DbModule.class
})

public interface TranslateAppComponent {
    void inject(MainActivity mainActivity);
}