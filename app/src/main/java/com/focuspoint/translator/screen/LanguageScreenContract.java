package com.focuspoint.translator.screen;

import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.presenters.BasePresenter;
import com.focuspoint.translator.views.BaseView;

import java.util.List;

/**
 * Contract for language choose screen;
 */

public class LanguageScreenContract {


    public interface View extends BaseView {

        void showLanguages(List<Language> list);
    }

    public interface Presenter extends BasePresenter <View>  {

        /**Obtain list of source languages, which will be presented for choose in view;*/
        void loadSourceLanguages();

        /**Obtain list of target languages, which will be presented for choose in view;*/
        void loadTargetLanguages();

    }
}
