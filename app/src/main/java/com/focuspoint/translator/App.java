package com.focuspoint.translator;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;


import com.focuspoint.translator.di.components.DaggerTranslateAppComponent;
import com.focuspoint.translator.di.components.TranslateAppComponent;
import com.focuspoint.translator.di.modules.DBModule;
import com.focuspoint.translator.di.modules.DataModule;
import com.focuspoint.translator.di.modules.AppModule;

/**
 * Custom application class;
 */

public class App extends Application{

    private TranslateAppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerTranslateAppComponent.builder()
                .dataModule(new DataModule(this))
                .appModule(new AppModule(this))
                .dBModule(new DBModule(this))
                .build();

    }

    public TranslateAppComponent getComponent() {
        return component;
    }

    public static App from(@NonNull Context context) {
        return (App) context.getApplicationContext();
    }

}



