package com.focuspoint.translator.models;

import java.util.ArrayList;
import java.util.List;
/**
 *
 */


public class Language{
    private String description;
    private String code;
    private List<Language> dirs; // but api can translate without dirs;

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
