package com.focuspoint.translator;

import com.focuspoint.translator.database.DB;
import com.focuspoint.translator.interactors.LanguageInteractor;
import com.focuspoint.translator.interactors.TranslationInteractor;
import com.focuspoint.translator.interactors.interfaces.IErrorInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Model;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.models.responseModels.DictionaryRM;
import com.focuspoint.translator.models.responseModels.TranslationRM;
import com.focuspoint.translator.network.DictionaryApiService;
import com.focuspoint.translator.network.TranslateApiService;
import com.focuspoint.translator.presenters.TranslationPresenter;
import com.focuspoint.translator.screen.TranslationScreenContract;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.HttpException;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */

@RunWith(MockitoJUnitRunner.class)
public class TranslationTest {
    TranslationInteractor translationInteractor;

    @Mock
    LanguageInteractor languageInteractor;

    TranslationPresenter presenter;

    Model model;

    @Mock
    TranslateApiService apiService;

    @Mock
    DB database;

    @Mock
    TranslationScreenContract.View view;

    @Mock
    TranslationScreenContract.Presenter presenterMock;

    @Mock
    TranslationInteractor translationInteractorMock;

    @Mock
    DictionaryApiService dictionaryApiService;

    @Mock
    IErrorInteractor errorInteractor;



    @Before
    public void init(){
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        model = new Model();

        translationInteractor = new TranslationInteractor(languageInteractor, apiService, model,
                database, dictionaryApiService, errorInteractor);

        presenter = new TranslationPresenter(translationInteractor, languageInteractor, errorInteractor);
    }




    @Test
    public void obtainLastTranslationWithoutDBandNetwork(){
        when(database.getLastTranslation()).thenReturn(Observable.just(null));
        when(languageInteractor.loadLanguages()).thenReturn(Observable.just(Language.obtainDefaultMap()));


        Translation result = translationInteractor
                .getLastTranslation()
                .toBlocking()
                .single();

        assertEquals(Translation.obtainDefault().getSource(), result.getSource());



    }


    @Test
    public void verifyViewShowOnLoad(){
        PublishSubject subject = PublishSubject.create();

        when(translationInteractorMock.getLastTranslation()).thenReturn(Observable.just(Translation.obtainDefault()));
        when(translationInteractorMock.getOnFavoriteSubject()).thenReturn(subject);
        when(translationInteractorMock.getOnSourceSubject()).thenReturn(subject);
        when(translationInteractorMock.getOnTargetSubject()).thenReturn(subject);
        when(translationInteractorMock.getOnTranslateSubject()).thenReturn(subject);
        when(errorInteractor.getErrorObservable()).thenReturn(subject.asObservable());


        TranslationPresenter presenter = new TranslationPresenter(translationInteractorMock, languageInteractor, errorInteractor);
        presenter.attach(view);


        presenter.loadTranslation();
        verify(view).showSource(any(Language.class));
    }


    @Test
    public void ignoreDictionary400Error(){

        Translation translation = Translation.obtainDefault();
        TranslationRM translationRM = new TranslationRM();
        translationRM.text = new ArrayList<>();
        translationRM.text.add("from server");

        when(languageInteractor.loadLanguages()).thenReturn(Observable.just(Language.obtainDefaultMap()));
        when(database.translate(translation)).thenReturn(Observable.just(null));
        when(dictionaryApiService.lookup(translation.getInput(), translation.getDirection()))
                .thenReturn(Observable.error(new Throwable("HTTP 400 BAD REQUEST")));
        when(apiService.translate(translation.getInput(), translation.getDirection())).thenReturn(Observable.just(translationRM));



        Translation result = translationInteractor.translate(translation)
                    .toBlocking()
                    .single();

        assertEquals("from server", result.getOutput());
    }

//    @Test
//    public void changeLanguageOnDisconnectMustShowError(){
//        TranslationRM translationRM = new TranslationRM();
//        translationRM.text = new ArrayList<>();
//        translationRM.text.add("from server");
//
//        when(database.getLastTranslation()).thenReturn(Observable.just(Translation.obtainDefault()));
//        when(languageInteractor.loadLanguages()).thenReturn(Observable.just(Language.obtainDefaultMap()));
//        when(database.translate(any())).thenReturn(Observable.just(null));
//
//        when(dictionaryApiService.lookup(any(), any()))
//                .thenReturn(Observable.error(new Throwable("something went wrong")));
//        when(apiService.translate(any(), any()))
//                .thenReturn(Observable.just(translationRM));
//
//
//
//
//
//        Language newLanguage = new Language();
//        newLanguage.setCode("az");
//        newLanguage.setDescription("someDescription");
//
//
//        Translation t =  translationInteractor.changeCurrentLanguage(null, newLanguage)
//                .toBlocking()
//                .single();
//        Mockito.verify(translationInteractor.addCurrentToHistory());
//
//
//    }




    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
    }



}
