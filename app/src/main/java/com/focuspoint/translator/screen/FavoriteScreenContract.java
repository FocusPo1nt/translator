package com.focuspoint.translator.screen;

/**
 * Created by v_banko on 3/31/2017.
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