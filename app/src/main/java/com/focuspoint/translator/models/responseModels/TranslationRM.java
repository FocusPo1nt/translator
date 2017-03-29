package com.focuspoint.translator.models.responseModels;

import com.focuspoint.translator.network.Values;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by root on 29.03.17.
 */

public class TranslationRM {
    @SerializedName(Values.CODE) public int code;
    @SerializedName(Values.LANG) public String lang;
    @SerializedName(Values.TEXT) public List<String> text;

}
