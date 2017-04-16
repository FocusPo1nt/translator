package com.focuspoint.translator.screen;

import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.presenters.BasePresenter;
import com.focuspoint.translator.views.BaseView;

import java.util.List;

/**
 * View & Presenter interfaces for favorite screen
 */

public class FavoriteScreenContract {



    public interface View extends BaseView {

        void showFavorites(List<Translation> translations);

        String getSearch();

        void goTo (Navigator screen);

    }

    public interface Presenter extends BasePresenter <View> {

        void setFavorite(Translation translation, boolean favorite);

        void load();

        void choose(Translation translation);

        void onSearchChanged(String text);

        void deleteAll();

        void deleteTranslation();
    }

}