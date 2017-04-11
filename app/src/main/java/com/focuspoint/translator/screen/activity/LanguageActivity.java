package com.focuspoint.translator.screen.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.focuspoint.translator.R;
import com.focuspoint.translator.adapters.LanguageAdapter;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.screen.LanguageScreenContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.subscriptions.CompositeSubscription;

/**
 * Base Activity for language pick
 */

public abstract class LanguageActivity extends AppCompatActivity implements LanguageScreenContract.View{
    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;

    protected Unbinder unbinder;
    protected LanguageAdapter adapter;
    protected CompositeSubscription subscriptions;


    protected void initViews(){
        subscriptions = new CompositeSubscription();

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
    public void showError(Throwable e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLanguages(List<Language> list) {
        adapter.replaceData(list);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        subscriptions.unsubscribe();
    }
}
