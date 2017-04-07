package com.focuspoint.translator.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by v_banko on 3/29/2017.
 */
@Data
public class Translation {


    private String input;
    private Language sourceLanguage;
    private Language targetLanguage;
    private String output;


    public Translation(Language sourceLanguage, Language targetLanguage){
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }

}
