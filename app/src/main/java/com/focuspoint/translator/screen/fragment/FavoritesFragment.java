package com.focuspoint.translator.screen.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.focuspoint.translator.App;
import com.focuspoint.translator.R;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.TranslationListContract;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;


/**
 * Fragment for viewpager, that shows favorites list
 */

public class FavoritesFragment extends TranslationListFragment implements TranslationListContract.FavoriteView{
    @Inject
    TranslationListContract.FavoritePresenter presenter;
    @BindString(R.string.favorites) String favoritesTitle;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.from(getContext()).getComponent().inject(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        title.setText(favoritesTitle);
    }

    public static FavoritesFragment newInstance(){
        return new FavoritesFragment();
    }

    @Override
    protected TranslationListContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void showFavorites(List<Translation> translations) {
        adapter.replaceData(translations);
    }


    @Override
    protected String getClearMessage() {
        return favoritesTitle;
    }
}
