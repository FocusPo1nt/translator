package com.focuspoint.translator.models;

import android.view.MotionEvent;

import javax.inject.Inject;

/**
 *
 */

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

    public void setCurrentOutput(String output){}

    public void setCurrentInput(String input){}

    public void setCurrentSource(Language source){}

    public void setCurrentTarget(Language target){}



    public void getCurrentOutput(String output){}

    public void getCurrentInput(String input){}

    public void getCurrentSource(Language source){}

    public void getCurrentTarget(Language target){}





}
