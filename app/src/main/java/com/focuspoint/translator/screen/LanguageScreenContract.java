package com.focuspoint.translator.screen;

import com.focuspoint.translator.presenters.BasePresenter;
import com.focuspoint.translator.views.BaseView;

/**
 * Contract for language choose view;
 */

public class LanguageScreenContract {


    public interface View extends BaseView {

        void showLanguages();
    }

    public interface Presenter extends BasePresenter <View>  {

       void loadLanguages();

    }
}
