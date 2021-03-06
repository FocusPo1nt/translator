package com.focuspoint.translator.models;


import com.focuspoint.translator.database.DB;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

/** Object which contains basic information about the act of translation;
 */

@StorIOSQLiteType(table = DB.Translations.TABLE)
public class Translation implements Cloneable{

    @StorIOSQLiteColumn(name = DB.Translations.INPUT, key = true)
    String input;

    @StorIOSQLiteColumn(name = DB.Translations.OUTPUT)
    String output;

    @StorIOSQLiteColumn(name = DB.Translations.DICTIONARY)
    String dictionary;

    @StorIOSQLiteColumn(name = DB.Translations.DATE)
    long date;

    @StorIOSQLiteColumn(name = DB.Translations.DIRECTION, key = true)
    String direction;

    @StorIOSQLiteColumn(name = DB.Translations.SOURCE)
    String source;

    @StorIOSQLiteColumn(name = DB.Translations.TARGET)
    String target;

    @StorIOSQLiteColumn(name = DB.Translations.STORAGE)
    int storage;


    private Language sourceLanguage;
    private Language targetLanguage;


    public static int STORAGE_DEFAULT = 0;
    public static int STORAGE_HISTORY = 1;
    public static int STORAGE_FAVORITE = 2;


    private static final String DEFAULT_INPUT = "«Hello world!»";
    private static final String DEFAULT_OUTPUT= "«Здравствуй, мир!»";


    public Translation(Language sourceLanguage, Language targetLanguage, String input, String output){
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.source = sourceLanguage.getCode();
        this.target = targetLanguage.getCode();
        this.input = input;
        this.output = output;
    }

    public Translation(){}

    /**Return String pair of languages
     * from source to target;*/
    public String getDirection(){
        if (sourceLanguage!=null && targetLanguage!=null){
            direction = sourceLanguage.getCode() + "-" + targetLanguage.getCode();
        }else if (source != null && target != null){
            direction =source + "-" + target;
        }else {
            direction = "unknown - unknown";
        }

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
        source = sourceLanguage.code; // set code from Language object
        this.sourceLanguage = sourceLanguage;
    }

    public Language getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(Language targetLanguage) {
        target = targetLanguage.getCode(); // set code from Language object
        this.targetLanguage = targetLanguage;
    }

    public String getOutput() {
        return output;
    }


    /**Add constant text to the end of translation result;*/
    public String getOutputWithWatermark(){
        //надо сделать константой;
        String watermark = "\n\nПереведено сервисом\n«Яндекс Переводчик»\nhttp://translate.yandex.ru";
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

        sb.append(getDirection()).append(" ");
        if (output!= null) sb.append(output).append(" ");

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
        String bufferCode = source;
        source = target;
        target = bufferCode;
        getDirection();
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }


    public Translation setStorage(int storage) {
        this.storage = storage;
        return this;
    }

    public void setFavorite(boolean favorite){
        storage = favorite? STORAGE_FAVORITE : STORAGE_HISTORY;
    }

    public boolean isFavorite() {
        return storage == STORAGE_FAVORITE;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Translation){
            Translation translation = (Translation) obj;
            return  (translation.input.equals(input)
                    && translation.output.equals(output)
                    && translation.direction.equals(direction));
        }else{
            return super.equals(obj);
        }
    }

    //TODO create escaping;
    public void prepareForSQLstorage(){
        input = input.replace("'","").replace(";","");
    }


    public static Translation obtainDefault(){
        Translation translation = new Translation();
        translation.setDate(System.currentTimeMillis());
        translation.setSourceLanguage(Language.obtainDefaultSource());
        translation.setTargetLanguage(Language.obtainDefaultTarget());
        translation.setInput(DEFAULT_INPUT);
        translation.setOutput(DEFAULT_OUTPUT);
        return translation;
    }


    public Translation setDictionary(String dictionary) {
        this.dictionary = dictionary;
        return this;
    }



    /**Add constant text to the end of translation result;*/
    public String getDictionaryWithWatermark(){

        String watermark = "\n\nРеализовано с помощью сервиса \n«Яндекс Словарь»\nhttps://tech.yandex.ru/dictionary/";
        String result = "";
        if (dictionary != null && !dictionary.isEmpty() && !dictionary.contains(watermark)){
            result = dictionary + watermark;
        }
        return result;
    }


}
