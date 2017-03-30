package com.focuspoint.translator.screen;

import com.focuspoint.translator.presenters.BasePresenter;
import com.focuspoint.translator.views.BaseView;

/**
 * Created by v_banko on 3/30/2017.
 */

public class MainScreenContract {

    interface View extends BaseView {
        void showResult();
        void showError();
    }

    interface Presenter extends BasePresenter {
        void translate();
        void loadLast();

    }
}