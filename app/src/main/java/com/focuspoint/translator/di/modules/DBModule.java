package com.focuspoint.translator.di.modules;



import com.focuspoint.translator.App;
import com.focuspoint.translator.database.DB;
import com.focuspoint.translator.database.DBOpenHelper;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.models.LanguageStorIOSQLiteDeleteResolver;
import com.focuspoint.translator.models.LanguageStorIOSQLiteGetResolver;
import com.focuspoint.translator.models.LanguageStorIOSQLitePutResolver;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 *
 */
@Module
public class DBModule {

    private App application;

    public DBModule(App application) {
        this.application = application;
    }





    @Provides
    @Singleton
    DB db(){
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new DBOpenHelper(application))
                .addTypeMapping(Language.class, SQLiteTypeMapping.<Language>builder()
                        .putResolver(new LanguageStorIOSQLitePutResolver())
                        .getResolver(new LanguageStorIOSQLiteGetResolver())
                        .deleteResolver(new LanguageStorIOSQLiteDeleteResolver())
                        .build())
                .build();


        return new DB(storIOSQLite);
    }
}