package com.focuspoint.translator.database;

import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.network.TranslateApiService;
import com.google.gson.internal.LinkedTreeMap;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Class that deals with the StorIO & rxjava;
 * Contains current database version;
 */

public class DB {
    public static final String NAME = "database";
    public static final int VERSION = 1;
    private StorIOSQLite database;

    @Inject
    public DB(StorIOSQLite storIOSQLite){
        this.database = storIOSQLite;
    }



    //    region TRANSLATIONS

    public Observable <List<Translation>> getTranslations(){
        return database.get()
                .listOfObjects(Translation.class)
                .withQuery(Query.builder()
                        .table(Translations.TABLE)
                        .orderBy(Translations.DATE + " DESC")
                        .build())
                .prepare()
                .asRxObservable();
    }


    public Observable <List<Translation>> getFavorites(){
        return database.get()
                .listOfObjects(Translation.class)
                .withQuery(Query.builder()
                        .table(Translations.TABLE)
                        .where(Translations.FAVORITE + " = 1")
                        .orderBy(Translations.DATE + " DESC")
                        .build())
                .prepare()
                .asRxObservable();
    }

    public Observable <Translation> translate(Translation translation){
        System.out.println("db request " + translation);
        //TODO сделать экранирование спец символов;
        return database.get()
                .object(Translation.class)
                .withQuery(Query.builder()
                        .table(Translations.TABLE)
                        .where(Translations.INPUT + " = '" + translation.getInput()
                                + "' AND " + Translations.DIRECTION + " = '" + translation.getDirection() + "'")
                        .build())
                .prepare()
                .asRxObservable()
                .doOnNext(translation1 -> System.out.println("db result " + translation1));
    }




    public Observable<Translation> getLastTranslation(){
        return database.get()
             .object(Translation.class)
                .withQuery(Query.builder()
                        .table(Translations.TABLE)
                        .where(Translations.DATE + " = (SELECT MAX (" + Translations.DATE + ") FROM " + Translations.TABLE + ")")
                        .build())
                .prepare()
                .asRxObservable();
    }

    public void saveDB(Translation translation){
        database
                .put()
                .object(translation)
                .prepare()
                .executeAsBlocking();
    }

    public void clearHistory(){
        database
                .delete()
                .byQuery(DeleteQuery.builder()
                        .table(Translations.TABLE)
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    public void clearFavorites(){
        database
                .delete()
                .byQuery(DeleteQuery.builder()
                        .table(Translations.TABLE)
                        .where(Translations.FAVORITE + " = 1")
                        .build())
                .prepare()
                .executeAsBlocking();

    }


    //    endregion

    public Observable <Map<String, Language>> getLanguages(){
        return database.get()
                .listOfObjects(Language.class)
                .withQuery(Query.builder()
                        .table(Languages.TABLE)
                        .build())
                .prepare()
                .asRxObservable()
                .map(languages -> {
                    Map <String, Language> map = new LinkedTreeMap<>() ;
                    for (Language language : languages){
                        map.put(language.getCode(), language);
                    }
                    return map;
                });
    }


    //    region LANGUAGES




    public void saveDB(List<Language> languages){
        System.out.println("save database");
        database
                .put()
                .objects(languages)
                .prepare()
                .executeAsBlocking();
    }


    //    endregion


    //Table for languages;
    public class Languages{
        public static final String TABLE = "languages";
        public static final String CODE = "_code";
        public static final String DESCRIPTION = "description";
    }

    //Table for translations;
    public class Translations{
        public static final String TABLE = "translations";
        public static final String INPUT = "_input";
        public static final String OUTPUT = "output";
        public static final String DIRECTION = "direction";
        public static final String SOURCE = "source";
        public static final String TARGET = "target";
        public static final String DATE = "date";
        public static final String FAVORITE = "favorite";
    }

    //Table that link source with available target languages;
    //No need;
    public class LangDirs{
        public static final String TABLE = "lang_dirs";
        public static final String SOURCE = "_source";
        public static final String TARGET = "target";
    }






}
