package com.focuspoint.translator.screen;

import com.focuspoint.translator.presenters.BasePresenter;
import com.focuspoint.translator.views.*;

/**
 * View & Presenter interfaces for history screen
 */
public class HistoryScreenContract {

    interface View extends BaseView {

        void showHistory();
    }

    interface Presenter extends BasePresenter {

        void addFavorite();

        void removeFavorite();

        void load();
    }
}