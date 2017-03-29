package com.focuspoint.translator.di.components;

import com.focuspoint.translator.MainActivity;
import com.focuspoint.translator.di.modules.ApiModule;

import javax.inject.Singleton;
import dagger.Component;

/**
 * Created by v_banko on 3/29/2017.
 */
@Singleton
@Component(modules = {
//        FoodSearchAppModule.class,
        ApiModule.class
//        DbModule.class
})
public interface TranslateAppComponent {
    void inject(MainActivity mainActivity);
}