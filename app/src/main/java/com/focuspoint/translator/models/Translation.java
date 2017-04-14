package com.focuspoint.translator.models;


import com.focuspoint.translator.database.DB;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

/** Object which contains basic information about the act of translation;
 */

@StorIOSQLiteType(table = DB.Translations.TABLE)
public class Translation {

    @StorIOSQLiteColumn(name = DB.Translations.INPUT, key = true)
    String input;

    private Language sourceLanguage;


    private Language targetLanguage;

    @StorIOSQLiteColumn(name = DB.Translations.OUTPUT)
    String output;

    @StorIOSQLiteColumn(name = DB.Translations.DATE)
    long date;

    @StorIOSQLiteColumn(name = DB.Translations.DIRECTION)
    String direction;


    public static final String DEFAULT_INPUT = "«Hello world!»";
    public static final String DEFAULT_OUTPUT= "«Здравствуй, мир!»";


    public Translation(Language sourceLanguage, Language targetLanguage, String input, String output){
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.input = input;
        this.output = output;
    }

    /**Return String pair of languages
     * from source to target;*/
    public String getDirection(){
        if (sourceLanguage == null
                || targetLanguage == null
                || sourceLanguage.getCode().isEmpty()
                || targetLanguage.getCode().isEmpty()){
            throw new IllegalStateException("Translation does not have source or target language");
        }

        direction = sourceLanguage.getCode() + "-" + targetLanguage.getCode();
        return direction;
    }



    public String getInput() {
        return input;
    }

    public Translation setInput(String input) {
        this.input = input;
        return this;
    }

    public Language getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(Language sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public Language getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(Language targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getOutput() {
        return output;
    }


    /**Add constant text to the end of translation result;*/
    public String getOutputWithWatermark(){
        String watermark = "\n\nПереведено сервисом «Яндекс Переводчик»\nhttp://translate.yandex.ru/";
        String result = "";
        if (!output.isEmpty() && !output.contains(watermark)){
            result = output + watermark;
        }
        return result;
    }

    public Translation setOutput(String output) {
        this.output = output;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (input !=null) sb.append(input).append(" ");
        if (sourceLanguage!= null && targetLanguage != null) sb.append(getDirection()).append(" ");
        if (output!= null) sb.append(output);
        return sb.toString();
    }


    /**input <-> output   source <-> target*/
    public void reverseLanguages(){
        Language bufferLanguage = sourceLanguage;
        sourceLanguage = targetLanguage;
        targetLanguage = bufferLanguage;
        String buffer = input;
        input = output;
        output = buffer;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
