package com.focuspoint.translator.di.modules;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.focuspoint.translator.App;
import com.focuspoint.translator.models.Model;
import com.focuspoint.translator.network.DictionaryApiService;
import com.focuspoint.translator.network.TranslateApiService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *Module for data providers;
 */

@Module
public class DataModule {

    private App application;

    public DataModule(App application) {
        this.application = application;
    }


    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .build();
    }

    @Provides
    @Singleton
    @Named("translate")
    Retrofit providesRetrofitTranslate(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(TranslateApiService.BASE_URL_TRNSL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    @Named("dictionary")
    Retrofit providesRetrofitDictionary(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(DictionaryApiService.BASE_URL_DICT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    TranslateApiService provideTranslationService(@Named("translate") Retrofit retrofit){
        return retrofit.create(TranslateApiService.class);
    }

    @Provides
    @Singleton
    DictionaryApiService provideDictionaryService(@Named("dictionary") Retrofit retrofit){
        return retrofit.create(DictionaryApiService.class);
    }

    @Provides
    @Singleton
    Model providesModel() {
        return new Model();
    }
}