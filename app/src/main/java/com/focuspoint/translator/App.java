package com.focuspoint.translator;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.focuspoint.translator.di.components.DaggerTranslateAppComponent;
import com.focuspoint.translator.di.components.TranslateAppComponent;
import com.focuspoint.translator.di.modules.ApiModule;

/**
 * Created by v_banko on 3/29/2017.
 */

public class App extends Application{

    private TranslateAppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerTranslateAppComponent.builder()
                .apiModule(new ApiModule())
                .build();
        System.out.println("skdlf");
    }

    public TranslateAppComponent getComponent() {
        return component;
    }

    public static App from(@NonNull Context context) {
        return (App) context.getApplicationContext();
    }

}



