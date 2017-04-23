package com.focuspoint.translator.models;

import javax.inject.Inject;


// Класс который содержит текущее состояние перевода;


public class Model {
    private Translation currentTranslation;



    @Inject
    public Model(){}


    public Translation getCurrentTranslation() {
        return currentTranslation;
    }

    public void setCurrentTranslation(Translation currentTranslation) {
        this.currentTranslation = currentTranslation;
    }

    public void setCurrentOutput(String output){
        if (currentTranslation != null){
            currentTranslation.setOutput(output);
        }

    }

    public void setDictionary(String dictionary){
        if (currentTranslation != null){
            currentTranslation.setDictionary(dictionary);
        }

    }


    public void setCurrentInput(String input){
        if (currentTranslation != null){
            currentTranslation.setInput(input);
        }
    }

    public void setFavorite(boolean favorite) {
        if (currentTranslation != null){
            currentTranslation.setFavorite(favorite);
        }

    }
}
