package com.focuspoint.translator.interactors;

import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;

import retrofit2.Retrofit;

/**
 * Created by root on 03.04.17.
 */

public class LanguageInteractor implements ILanguageInteractor {
    private Retrofit retrofit;

    public LanguageInteractor(Retrofit retrofit){
        this.retrofit = retrofit;
    }


}
