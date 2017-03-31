package com.focuspoint.translator.screen;

import com.focuspoint.translator.presenters.*;
import com.focuspoint.translator.views.*;

/**
 * Created by v_banko on 3/31/2017.
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