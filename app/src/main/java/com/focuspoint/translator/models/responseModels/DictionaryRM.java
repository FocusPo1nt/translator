package com.focuspoint.translator.models.responseModels;

import com.focuspoint.translator.network.Values;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DictionaryRM {
    @SerializedName(Values.DEF) public List<DicDef> defList;

    class DicDef{
        @SerializedName(Values.TR) public List<DicTr> trList;
    }

    class DicTr{

        @SerializedName(Values.POS) public String pos;
        @SerializedName(Values.TEXT) public String result;
        @SerializedName(Values.MEAN) public List<DicText> means;
    }

    class DicText{
        @SerializedName(Values.TEXT) public String text;
    }

    public String toString(){
        if (defList == null || defList.isEmpty()) return "";
        StringBuilder stringBuilder = new StringBuilder();
        int count = 1;

        for (DicDef dic: defList){
            if (dic.trList == null || dic.trList.isEmpty()) continue;

            for (DicTr tr : dic.trList){
                stringBuilder.append(count + " ").append(tr.result);
                if (tr.means != null && !tr.means.isEmpty()){
                    stringBuilder.append(" (");

                    for (int i = 0; i < tr.means.size(); i++){
                        stringBuilder.append(tr.means.get(i).text)
                                .append(i == tr.means.size() - 1 ? "" : ", ");
                    }
                    stringBuilder.append(")");
                }
                count++;
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }



}
