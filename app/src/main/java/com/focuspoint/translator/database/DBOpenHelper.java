package com.focuspoint.translator.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Inject;

/**
 * OpenHelper class;
 * Init database;
 * Database version control;
 */

public class DBOpenHelper extends SQLiteOpenHelper{


    @Inject
    public DBOpenHelper(Context context) {
        super(context, DB.NAME, null, DB.VERSION);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTables(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + DB.Languages.TABLE + " ("
                + DB.Languages.CODE + " TEXT PRIMARY KEY, "
                + DB.Languages.DESCRIPTION    + " TEXT);");

        db.execSQL("create table " + DB.Translations.TABLE + " ("
                + DB.Translations.INPUT + " TEXT, "
                + DB.Translations.DIRECTION  + " TEXT, "
                + DB.Translations.OUTPUT     + " TEXT, "
                + DB.Translations.FAVORITE     + " INTEGER, "
                + DB.Translations.DATE   + " INTEGER);");


        db.execSQL("create table " + DB.LangDirs.TABLE + " ("
                + DB.LangDirs.SOURCE + " TEXT, "
                + DB.LangDirs.TARGET  + " TEXT);");
    }
}