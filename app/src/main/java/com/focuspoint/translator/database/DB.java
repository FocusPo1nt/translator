package com.focuspoint.translator.database;

import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.network.TranslateApiService;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.functions.Func1;

/**
 * Database class with tables
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
                        .build())
                .prepare()
                .asRxObservable();
    }

    public Observable<Translation> getLastTranslation(){
        return database.get()
             .listOfObjects(Translation.class)
                .withQuery(Query.builder()
                        .table(Translations.TABLE)
                        .where( "MAX(" + Translations.DATE + ")")
                        .build())
                .prepare()
                .asRxObservable()
                .flatMap(translations -> translations.size() > 0 ?
                        Observable.from(translations).first() : Observable.empty());
    }

    public Single<PutResult> saveDB(Translation translation){
        return database
                .put()
                .object(translation)
                .prepare()
                .asRxSingle();
    }


    //    endregion


    //    region LANGUAGES

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
        public static final String DATE = "date";
        public static final String FAVORITE = "favorite";
    }

    //Table that link source with available target languages;
    public class LangDirs{
        public static final String TABLE = "lang_dirs";
        public static final String SOURCE = "_source";
        public static final String TARGET = "target";
    }






}
