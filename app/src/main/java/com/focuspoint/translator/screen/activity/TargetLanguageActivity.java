package com.focuspoint.translator.screen.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.focuspoint.translator.App;
import com.focuspoint.translator.R;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.screen.LanguageScreenContract;

import java.util.List;

import javax.inject.Inject;

public class TargetLanguageActivity extends LanguageActivity {

    @Inject
    LanguageScreenContract.Presenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.from(this).getComponent().inject(this);

        initViews();

        presenter.attach(this);
        presenter.loadTargetLanguages();
    }

    @Override
    protected void initViews() {
        super.initViews();

        subscriptions.add(adapter.getPositionClicks()
                .subscribe(language -> {
                    presenter.onTargetChanged(language);
                    finish();
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }

}
