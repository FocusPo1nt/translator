package com.focuspoint.translator.screen;

import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.presenters.BasePresenter;
import com.focuspoint.translator.views.*;

import java.util.List;

/**
 * View & Presenter interfaces for history screen
 */
public class HistoryScreenContract {

    public interface View extends BaseView {

        void showHistory(List<Translation> translations);
    }

    public interface Presenter extends BasePresenter <View> {

        void addFavorite();

        void removeFavorite();

        void load();
    }
}