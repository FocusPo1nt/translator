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
}
