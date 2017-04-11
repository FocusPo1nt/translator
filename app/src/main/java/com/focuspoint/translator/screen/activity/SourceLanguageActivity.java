package com.focuspoint.translator.screen.activity;

import android.os.Bundle;
import android.widget.Toast;


import com.focuspoint.translator.App;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.screen.LanguageScreenContract;

import javax.inject.Inject;

public class SourceLanguageActivity extends LanguageActivity {


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
    protected void initViews() {
        super.initViews();

        subscriptions.add(adapter.getPositionClicks()
                .subscribe(language -> {
                    presenter.onSourceChanged(language);
                    finish();
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }
}
