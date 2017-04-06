package com.focuspoint.translator.screen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;


import com.focuspoint.translator.App;
import com.focuspoint.translator.R;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.screen.LanguageScreenContract;

import java.util.List;

import javax.inject.Inject;

public class LanguageActivity extends AppCompatActivity implements LanguageScreenContract.View{

    @Inject LanguageScreenContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.from(this).getComponent().inject(this);

        initViews();

        presenter.attach(this);
        presenter.loadSourceLanguages();

    }

    @Override
    public void showError(Throwable e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLanguages(List<Language> list) {
        Toast.makeText(this, list.get(5).getDescription(), Toast.LENGTH_SHORT).show();
    }


    private void initViews(){
        setContentView(R.layout.activity_language);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
