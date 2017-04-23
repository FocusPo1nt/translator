package com.focuspoint.translator.presenters;

import com.focuspoint.translator.interactors.interfaces.ILanguageInteractor;
import com.focuspoint.translator.interactors.interfaces.ITranslationInteractor;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.screen.LanguageScreenContract;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


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
                .map(map -> {
                    List<Language> list = new ArrayList<>(map.values());
                    Collections.sort(list,
                            (l1, l2) -> l1.getDescription().compareTo(l2.getDescription()));
                    return list;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        languages -> view.get().showLanguages(languages),
                        throwable -> view.get().showError(throwable))
        );
    }

    @Override
    public void loadTargetLanguages() {

        subscriptions.add(languageInteractor.loadLanguages()
                .subscribeOn(Schedulers.io())
                .map(map -> {
                    List<Language> list = new ArrayList<>(map.values());
                    Collections.sort(list,
                            (l1, l2) -> l1.getDescription().compareTo(l2.getDescription()));
                    return list;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        languages -> view.get().showLanguages(languages),
                        throwable -> view.get().showError(throwable))
        );
    }


    @Override
    public void onSourceChanged(Language source) {
        subscriptions.add(translationInteractor.changeCurrentLanguage(source, null)
                .subscribe(translation -> {}, this::handleError));
    }

    @Override
    public void onTargetChanged(Language target) {
        subscriptions.add(translationInteractor.changeCurrentLanguage(null, target)
                .subscribe(translation -> {}, this::handleError));
    }

    private void handleError(Throwable t){
        System.out.println("language presenter " + t);
    }
}
