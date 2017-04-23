package com.focuspoint.translator.database;

import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.Translation;
import com.google.gson.internal.LinkedTreeMap;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * Class that deals with the StorIO & rxjava;
 * Contains current database version;
 *
 * Мне очень нравится у StorIO возможность подписаться на обновление базы;
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

    public Observable <List<Translation>> getHistory(){
        return database.get()
                .listOfObjects(Translation.class)
                .withQuery(Query.builder()
                        .table(Translations.TABLE)
                        .where(Translations.STORAGE + " = " + Translation.STORAGE_HISTORY + " OR " +
                                Translations.STORAGE + " = " + Translation.STORAGE_FAVORITE)
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
                        .where(Translations.STORAGE + " = " + Translation.STORAGE_FAVORITE)
                        .orderBy(Translations.DATE + " DESC")
                        .build())
                .prepare()
                .asRxObservable();
    }


    /**Search translation in database*/
    public Observable <Translation> translate(Translation translation){

        //Перед запросом на сервер смотрим базу (вместе с избранным и историей
        // все ответы с сервера сохраняются в базу)

        //TODO сделать экранирование спец символов;
        //На данный момент символы ' и ; удаляются;
        return database.get()
                .object(Translation.class)
                .withQuery(Query.builder()
                        .table(Translations.TABLE)
                        .where(Translations.INPUT + " = '" + translation.getInput()
                                + "' AND " + Translations.DIRECTION + " = '" + translation.getDirection() + "'")
                        .build())
                .prepare()
                .asRxObservable();
    }



    /**obtain translation with max date value*/
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

    public void save(Translation translation){
        database
                .put()
                .object(translation)
                .prepare()
                .executeAsBlocking();
    }

    //TODO don't remove from database;
    public void clearHistory(){
        //Сейчас все что соответствует статусу истории удаляется и теряется возможность
        //брать из базы при отсутствии интернета;

        database
                .delete()
                .byQuery(DeleteQuery.builder()
                        .table(Translations.TABLE)
                        .where(Translations.STORAGE + " = " + Translation.STORAGE_HISTORY)
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    //TODO don't remove from database;
    public void clearFavorites(){
        database
                .delete()
                .byQuery(DeleteQuery.builder()
                        .table(Translations.TABLE)
                        .where(Translations.STORAGE + " = " + Translation.STORAGE_FAVORITE)
                        .build())
                .prepare()
                .executeAsBlocking();
    }


    //    endregion

    //    region LANGUAGES

    public Observable <Map<String, Language>> getLanguages(){

        //приводим результат из базы к тому же виду, что парсится от сервера;
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



    public void saveDB(List<Language> languages){
        database
                .put()
                .objects(languages)
                .prepare()
                .executeAsBlocking();
    }


    //    endregion


    //Названия и поля таблиц

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
        public static final String DICTIONARY = "dictionary";
        public static final String DIRECTION = "direction";
        public static final String SOURCE = "source";
        public static final String TARGET = "target";
        public static final String DATE = "date";
        public static final String STORAGE = "storage";
    }

    //Table that link source with available target languages;
    //No need;
    public class LangDirs{
        public static final String TABLE = "lang_dirs";
        public static final String SOURCE = "_source";
        public static final String TARGET = "target";
    }






}
