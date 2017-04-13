package com.focuspoint.translator.models;

import com.focuspoint.translator.database.DB;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.ArrayList;
import java.util.List;
/**
 *
 */

@StorIOSQLiteType(table = DB.Languages.TABLE)
public class Language{

    @StorIOSQLiteColumn(name = DB.Languages.CODE, key = true)
    String code;

    @StorIOSQLiteColumn(name = DB.Languages.DESCRIPTION)
    String description;




    private List<Language> dirs; // however api can translate without dirs;

    public Language(){
        dirs = new ArrayList<>();
    }

    public void addDir(Language language){
        dirs.add(language);
    }



    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }


    //TODO private collection;
    public List<Language> getDirs() {
        return dirs;
    }
}
