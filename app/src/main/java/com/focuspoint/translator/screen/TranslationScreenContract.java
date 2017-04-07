package com.focuspoint.translator.screen;

import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.presenters.BasePresenter;
import com.focuspoint.translator.views.BaseView;

/**
 * View & Presenter interfaces for main screen
 */

public class TranslationScreenContract {

    public interface View extends BaseView {

        void showInput(String text);

        void showOutput(String text);

        void showSource(Language language);

        void showTarget(Language language);

        void showFullScreen();

        void showInputSpeechToText();

        void showInputTextToSpeech();

        void showOutputTextToSpeech();

        void showAddToFavorites();

        void showShare();
    }

    public interface Presenter extends BasePresenter <View>{

        void translate(String text);

        void loadTranslation();
    }
}