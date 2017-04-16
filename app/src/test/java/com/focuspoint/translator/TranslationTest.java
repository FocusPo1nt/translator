package com.focuspoint.translator;

import com.focuspoint.translator.database.DB;
import com.focuspoint.translator.interactors.LanguageInteractor;
import com.focuspoint.translator.interactors.TranslationInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Model;
import com.focuspoint.translator.models.Translation;
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

import java.util.Map;

import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by root on 16.04.17.
 */

@RunWith(MockitoJUnitRunner.class)
public class TranslationTest {
    TranslationInteractor translationInteractor;

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



    @Before
    public void init(){
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        model = new Model();

        languageInteractor  = new LanguageInteractor(apiService, database);
        translationInteractor = new TranslationInteractor(languageInteractor, apiService, model, database);

        presenter = new TranslationPresenter(translationInteractor, languageInteractor);
    }

    @Test
    public void obtainLastTranslationWithoutDBandNetwork(){
        when(database.getLastTranslation()).thenReturn(Observable.just(null));
        when(database.getLanguages()).thenReturn(Observable.just(null));
        when(apiService.getLangs(any(String.class))).thenReturn(Observable.error(new Throwable(" ")));

        presenter.attach(view);
        presenter.loadTranslation();

        TestSubscriber <Translation>subscriber = new TestSubscriber<>();

        subscriber.add(translationInteractor
                .getLastTranslation()
                .subscribe(translation -> {
                    Assert.assertNotNull(translation);
                    Assert.assertNotNull(translation.getSource());
                    Assert.assertNotNull(translation.getTargetLanguage());

                }));
        System.out.println("of");


    }


    @Test
    public void verifyViewShowOnLoad(){
        PublishSubject subject = PublishSubject.create();

        when(translationInteractorMock.getLastTranslation()).thenReturn(Observable.just(Translation.obtainDefault()));
        when(translationInteractorMock.getOnFavoriteSubject()).thenReturn(subject);
        when(translationInteractorMock.getOnSourceSubject()).thenReturn(subject);
        when(translationInteractorMock.getOnTargetSubject()).thenReturn(subject);
        when(translationInteractorMock.getOnTranslateSubject()).thenReturn(subject);


        TranslationPresenter presenter = new TranslationPresenter(translationInteractorMock, languageInteractor);
        presenter.attach(view);


        presenter.loadTranslation();
        verify(view).showSource(any(Language.class));
    }


    @Test
    public void translateWithoutDBandNetwork(){


    }




    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
    }



}
