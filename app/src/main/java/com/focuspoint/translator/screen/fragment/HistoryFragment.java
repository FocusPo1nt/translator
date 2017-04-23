package com.focuspoint.translator.screen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.focuspoint.translator.App;
import com.focuspoint.translator.R;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.TranslationListContract;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;


/**
 * Fragment for viewpager, that shows translation historyTitle list
 */

public class HistoryFragment extends TranslationListFragment implements TranslationListContract.HistoryView{

    @Inject
    TranslationListContract.HistoryPresenter presenter;
    @BindString(R.string.history) String historyTitle;
    @BindString(R.string.clear_history) String clearHistory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.from(getContext()).getComponent().inject(this);
    }

    public static HistoryFragment newInstance(){
        return new HistoryFragment();
    }

    @Override
    protected void initViews() {
        super.initViews();
        title.setText(historyTitle);
    }

    @Override
    protected TranslationListContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void showHistory(List<Translation> translations) {
        adapter.replaceData(translations);
    }

    @Override
    protected String getClearMessage() {
        return clearHistory;
    }
}
