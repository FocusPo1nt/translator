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

        String getInput();
    }

    public interface Presenter extends BasePresenter <View>{

        /**if input is not empty -> try to translate text;*/
        void onInputChanged(String text);

        /**change view when source change -> try to translate text;*/
        void onSourceChanged(Language language);

        /**change view when target change -> try to translate text;*/
        void onTargetChange(Language language);

        /**presenter fills view with last translation data;*/
        void loadTranslation();

        /**on change source and target languages;*/
        void reverseLanguages();

    }
}