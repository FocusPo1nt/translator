package com.focuspoint.translator.screen;

import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.presenters.BasePresenter;
import com.focuspoint.translator.views.*;

import java.util.List;

/**
 * View & Presenter interfaces for history screen
 */
public class TranslationListContract {


    public interface Presenter <V> extends BasePresenter <V>  {

        void setFavorite(Translation translation, boolean favorite);

        void load();

        void choose(Translation translation);

        void onSearchChanged(String text);

        void deleteAll();

        void deleteTranslation();

        void clearAll();
    }

    public interface HistoryView extends View{

        void showHistory(List<Translation> translations);
    }

    public interface FavoriteView extends View{

        void showFavorites(List<Translation> translations);
    }

    public interface HistoryPresenter extends Presenter<HistoryView>{}

    public interface FavoritePresenter extends Presenter<FavoriteView>{}

    public interface View extends BaseView {

        String getSearch();

        void goTo (Navigator screen);

    }



}