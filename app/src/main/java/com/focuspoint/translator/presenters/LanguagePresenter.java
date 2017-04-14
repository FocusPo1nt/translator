package com.focuspoint.translator.presenters;

import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.screen.LanguageScreenContract;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 *
 */

public class LanguagePresenter implements LanguageScreenContract.Presenter {

    private CompositeSubscription subscriptions;
    private ILanguageInteractor languageInteractor;
    private WeakReference<LanguageScreenContract.View> view;
    private ITranslationInteractor translationInteractor;

    public LanguagePresenter(ILanguageInteractor languageInteractor, ITranslationInteractor translationInteractor){
        this.languageInteractor = languageInteractor;
        this.translationInteractor = translationInteractor;
    }

    @Override
    public void attach(LanguageScreenContract.View view) {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        subscriptions = new CompositeSubscription();
        this.view = new WeakReference<>(view);
    }

    @Override
    public void detach() {
        if (subscriptions!= null)  subscriptions.unsubscribe();
        view.clear();
    }

    @Override
    public void loadSourceLanguages() {
        subscriptions.add(languageInteractor.loadLanguages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(map -> new ArrayList<>(map.values()))
                .subscribe(
                        languages -> view.get().showLanguages(languages),
                        throwable -> view.get().showError(throwable))
        );
    }

    @Override
    public void loadTargetLanguages() {

        subscriptions.add(languageInteractor.loadLanguages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(map -> new ArrayList<>(map.values()))
                .subscribe(
                        languages -> view.get().showLanguages(languages),
                        throwable -> view.get().showError(throwable))
        );


//        subscriptions.add(languageInteractor.loadLanguages()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .withLatestFrom(translationInteractor.getLastTranslation(), (map, translation)
//                        -> map.get(translation.getSourceLanguage().getCode()).getDirs())
//                .subscribe(
//                        languages -> view.get().showLanguages(languages),
//                        throwable -> view.get().showError(throwable))
//        );
    }


    @Override
    public void onSourceChanged(Language language) {
        translationInteractor.onSourceChanged(language)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        translation -> {
                            System.out.println(translationInteractor.getOnTranslateSubject().toString());
                        },

                        System.out::println);
    }

    @Override
    public void onTargetChanged(Language language) {
        translationInteractor.onTargetChanged(language)
                .subscribe(
                        translation -> {},
                        System.out::println);
    }
}
