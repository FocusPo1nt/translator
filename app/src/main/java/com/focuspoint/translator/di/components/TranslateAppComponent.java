package com.focuspoint.translator.di.components;

import com.focuspoint.translator.screen.activity.SourceLanguageActivity;
import com.focuspoint.translator.screen.activity.MainActivity;
import com.focuspoint.translator.di.modules.DataModule;
import com.focuspoint.translator.di.modules.AppModule;
import com.focuspoint.translator.screen.activity.TargetLanguageActivity;
import com.focuspoint.translator.screen.fragment.TranslateFragment;

import javax.inject.Singleton;
import dagger.Component;

/**
 *
 */
@Singleton
@Component(modules = {
        AppModule.class,
        DataModule.class
//        DbModule.class
})

public interface TranslateAppComponent {
    void inject (MainActivity mainActivity);
    void inject (SourceLanguageActivity sourceLanguageActivity);
    void inject (TargetLanguageActivity targetLanguageActivity);
    void inject (TranslateFragment translateFragment);
}