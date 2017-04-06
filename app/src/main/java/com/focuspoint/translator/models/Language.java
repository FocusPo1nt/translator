package com.focuspoint.translator.models;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by root on 01.04.17.
 */


public class Language {
    private String description;
    private String code;
    private List<Language> dirs;

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



}
