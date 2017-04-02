package com.focuspoint.translator.screen;

import com.focuspoint.translator.views.*;

/**
 * View & Presenter interfaces for history screen
 */
public class HistoryScreenContract {

    interface View extends BaseView {

        void showHistory();
    }

    interface Presenter extends BaseView {

        void addFavorite();

        void removeFavorite();

        void load();
    }
}