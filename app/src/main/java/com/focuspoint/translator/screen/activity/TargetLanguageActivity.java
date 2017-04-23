package com.focuspoint.translator.screen.activity;

import android.os.Bundle;

import com.focuspoint.translator.App;
import com.focuspoint.translator.screen.LanguageScreenContract;


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
