package com.focuspoint.translator.models;


/**
 * Created by v_banko on 3/29/2017.
 */

public class Translation {


    private String input;
    private Language sourceLanguage;
    private Language targetLanguage;
    private String output;


    public Translation(Language sourceLanguage, Language targetLanguage){
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }

    public String getDirection(){
        if (sourceLanguage == null
                || targetLanguage == null
                || sourceLanguage.getCode().isEmpty()
                || targetLanguage.getCode().isEmpty()){
            throw new IllegalStateException("Translation does not have source or target language");
        }
        return sourceLanguage.getCode() + "-" + targetLanguage.getCode();
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
        if (!output.isEmpty()){
            output = output + "\n\nПереведено сервисом «Яндекс.Переводчик»\nhttp://translate.yandex.ru/";
        }
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
