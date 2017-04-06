package com.focuspoint.translator.screen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import com.focuspoint.translator.App;
import com.focuspoint.translator.R;
import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.screen.LanguageScreenContract;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class LanguageActivity extends AppCompatActivity implements LanguageScreenContract.View{

    @Inject LanguageScreenContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.from(this).getComponent().inject(this);

        initViews();

        presenter.attach(this);
        presenter.loadLanguages();

    }

    @Override
    public void showError(Throwable e) {

    }

    @Override
    public void showLanguages() {

    }

    private void initViews(){
        setContentView(R.layout.activity_language);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
