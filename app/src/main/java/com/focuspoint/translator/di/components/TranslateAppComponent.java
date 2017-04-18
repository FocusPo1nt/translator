package com.focuspoint.translator.di.components;

import com.focuspoint.translator.di.modules.DBModule;
import com.focuspoint.translator.screen.activity.SourceLanguageActivity;
import com.focuspoint.translator.screen.activity.MainActivity;
import com.focuspoint.translator.di.modules.DataModule;
import com.focuspoint.translator.di.modules.AppModule;
import com.focuspoint.translator.screen.activity.TargetLanguageActivity;
import com.focuspoint.translator.screen.fragment.FavoritesFragment;
import com.focuspoint.translator.screen.fragment.HistoryFragment;
import com.focuspoint.translator.screen.fragment.TranslateFragment;
import com.focuspoint.translator.screen.fragment.TranslationListFragment;


import javax.inject.Singleton;
import dagger.Component;

/**
 *
 */
@Singleton
@Component(modules = {
        AppModule.class,
        DataModule.class,
        DBModule.class
})

public interface TranslateAppComponent {
    void inject (MainActivity mainActivity);
    void inject (SourceLanguageActivity sourceLanguageActivity);
    void inject (TargetLanguageActivity targetLanguageActivity);
    void inject (TranslateFragment translateFragment);
    void inject (HistoryFragment historyFragment);
    void inject (FavoritesFragment favoritesFragment);
    void inject (TranslationListFragment favoritesFragment);

}