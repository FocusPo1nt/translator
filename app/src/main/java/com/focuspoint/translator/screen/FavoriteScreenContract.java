package com.focuspoint.translator.screen;

import com.focuspoint.translator.views.BaseView;

/**
 * View & Presenter interfaces for favorite screen
 */

public class FavoriteScreenContract {

    interface View extends BaseView {

        void showFavorite();
    }

    interface Presenter extends BaseView {

        void addFavorite();

        void removeFavorite();

        void load();
    }
}