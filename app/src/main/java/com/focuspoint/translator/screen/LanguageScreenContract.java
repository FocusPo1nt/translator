package com.focuspoint.translator.screen;

import com.focuspoint.translator.presenters.BasePresenter;
import com.focuspoint.translator.views.BaseView;

/**
 * Contract for language choose view;
 */

public class LanguageScreenContract {


    interface View extends BaseView {

        void showLanguages();
    }

    interface Presenter extends BasePresenter <View>  {

       void loadLanguages();

    }
}
