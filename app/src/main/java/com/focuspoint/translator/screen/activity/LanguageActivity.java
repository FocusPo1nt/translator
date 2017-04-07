package com.focuspoint.translator.screen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;


import com.focuspoint.translator.App;
import com.focuspoint.translator.R;
import com.focuspoint.translator.adapters.LanguageAdapter;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.screen.LanguageScreenContract;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LanguageActivity extends AppCompatActivity implements LanguageScreenContract.View{

    @Inject LanguageScreenContract.Presenter presenter;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private Unbinder unbinder;
    private LanguageAdapter adapter;



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
        adapter.replaceData(list);
    }


    private void initViews(){
        setContentView(R.layout.activity_language);
        unbinder = ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Language> languageList = new ArrayList<>();
        adapter = new LanguageAdapter(languageList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();


    }
}
