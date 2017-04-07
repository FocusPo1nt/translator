package com.focuspoint.translator.screen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.focuspoint.translator.App;
import com.focuspoint.translator.R;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.screen.TranslationScreenContract;
import com.focuspoint.translator.screen.activity.LanguageActivity;

import java.net.UnknownHostException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main view, where you can translate your sentence;
 */

public class TranslateFragment extends Fragment implements TranslationScreenContract.View{


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.from_language) TextView sourceLanguageView;
    @BindView(R.id.to_language) TextView targetLanguageView;

    @Inject TranslationScreenContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_translation, container, false);
        ButterKnife.bind(this, v);
        App.from(getContext()).getComponent().inject(this);


        presenter.attach(this);
        presenter.loadTranslation();

        sourceLanguageView.setOnClickListener(v1 -> {
            Intent intent = new Intent(getContext(), LanguageActivity.class);
            getActivity().startActivity(intent);
        });
        return v;
    }

    public static TranslateFragment newInstance(){
        return new TranslateFragment();
    }


    @Override
    public void showError(Throwable e) {

        if (e instanceof UnknownHostException){
            Toast.makeText(getContext(), "отсутствует связь с сервером", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void showInput(String text) {

    }

    @Override
    public void showOutput(String text) {

    }

    @Override
    public void showSource(Language language) {
        sourceLanguageView.setText(language.getDescription());
    }

    @Override
    public void showTarget(Language language) {
        targetLanguageView.setText(language.getDescription());
    }



    @Override
    public void showFullScreen() {

    }

    @Override
    public void showInputSpeechToText() {

    }

    @Override
    public void showInputTextToSpeech() {

    }

    @Override
    public void showOutputTextToSpeech() {

    }

    @Override
    public void showAddToFavorites() {

    }

    @Override
    public void showShare() {

    }
}
