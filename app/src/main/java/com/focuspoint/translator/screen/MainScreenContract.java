package com.focuspoint.translator.screen;

import com.focuspoint.translator.presenters.BasePresenter;
import com.focuspoint.translator.views.BaseView;

/**
 * View & Presenter interfaces for main screen
 */

public class MainScreenContract {

    interface View extends BaseView {

        void showInput(String text);

        void showOutput(String text);

        void showError();

        void showFullScreen();

        void showInputSpeechToText();

        void showInputTextToSpeech();

        void showOutputTextToSpeech();

        void showAddToFavorites();

        void showShare();
    }

    interface Presenter extends BasePresenter {

        void translate(String text);

        void loadLast();
    }
}