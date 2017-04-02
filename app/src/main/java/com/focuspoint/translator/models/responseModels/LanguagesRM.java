package com.focuspoint.translator.models.responseModels;

import com.focuspoint.translator.models.Language;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;
import java.util.Map;


/**
 * Gets the language json information from the server
 * and converts it to Map of Language objects
 */


public class LanguagesRM {
    @SerializedName("langs") public Map <String, String> descriptions;
    @SerializedName("dirs")  public List <String> dirs;

    public Map<String, Language> obtainLanguages(){
        Map <String, Language> map = new LinkedTreeMap<>() ;
        for (String code: descriptions.keySet()){
            Language language = new Language();
            language.setCode(code);
            language.setDescription(descriptions.get(code));
            map.put(code, language);
        }
        makeDirs(map);
        return map;
    }


    /**Convert 'dirs' array to link between Languages*/
    private void makeDirs (Map<String, Language> map){
        if (dirs != null){
            for (String dir : dirs){
                String pair[] = dir.split("-");
                if (pair.length == 2){
                    map.get(pair[0]).addDir(map.get(pair[1]));
                }
            }
        }
    }

}
