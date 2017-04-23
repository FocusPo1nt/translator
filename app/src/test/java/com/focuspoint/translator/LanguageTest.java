package com.focuspoint.translator;

import android.support.design.widget.TabLayout;

import com.focuspoint.translator.database.DB;
import com.focuspoint.translator.interactors.LanguageInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.network.TranslateApiService;

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

import static org.mockito.ArgumentMatchers.any;

/**
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class LanguageTest {
    LanguageInteractor languageInteractor;

    @Mock
    TranslateApiService apiService;

    @Mock
    DB database;

    @Before
    public void init(){
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });


        languageInteractor = new LanguageInteractor(apiService, database);
    }

    @Test
    public void obtainLanguagesWithoutDBandNetwork(){
        Mockito.when(database.getLanguages()).thenReturn(Observable.just(null));
        Mockito.when(apiService.getLangs(any(String.class))).thenReturn(Observable.error(new Throwable(" ")));


        TestSubscriber<Map<String, Language>> testSubscriber = new TestSubscriber<>();
        testSubscriber.add(languageInteractor
                .loadLanguages()
                .subscribe(
                        map -> {
                            Assert.assertNotNull(map);
                            Assert.assertNotNull(map.get(Language.DEFAULT_SOURCE_CODE));
                            Assert.assertNotNull(map.get(Language.DEFAULT_TARGET_CODE));
                        }));
        testSubscriber.assertNoErrors();
    }



    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
    }

}
