package com.focuspoint.translator.screen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.focuspoint.translator.App;
import com.focuspoint.translator.R;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.screen.TranslationScreenContract;
import com.focuspoint.translator.screen.activity.SourceLanguageActivity;
import com.focuspoint.translator.screen.activity.TargetLanguageActivity;
import com.jakewharton.rxbinding.widget.RxTextView;


import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Main view, where you can translate your word/sentence;
 */

public class TranslateFragment extends Fragment implements TranslationScreenContract.View{


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.from_language) TextView sourceLanguageView;
    @BindView(R.id.to_language) TextView targetLanguageView;
    @BindView(R.id.input_edit_text) EditText inputEditText;
    @BindView(R.id.output_text_view) TextView outputTextView;
    @BindView(R.id.reverse_image_view) ImageView reverseImageView;
    @BindView(R.id.star_image) ImageView starImageView;
    @BindView(R.id.favorite_frame) FrameLayout favoriteFrame;





    @Inject TranslationScreenContract.Presenter presenter;
    private CompositeSubscription subscriptions;
    private boolean ignoreInput = false; //flag to prevent api call when change input programmatically

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        App.from(getContext()).getComponent().inject(this);
        subscriptions = new CompositeSubscription();
        View view = inflater.inflate(R.layout.fragment_translation, container, false);
        ButterKnife.bind(this, view);

        initViews();


        return view;
    }

    public static TranslateFragment newInstance(){
        return new TranslateFragment();
    }


    @Override
    public void showError(Throwable e) {
        System.out.println(e);
        if (e instanceof UnknownHostException){
            Toast.makeText(getContext(), "отсутствует связь с сервером", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void showInput(String text) {
        if (!inputEditText.getText().toString().equals(text)){
            int cursor = inputEditText.getSelectionStart();
            ignoreInput = true;
            inputEditText.setText(text);
            inputEditText.setSelection(Math.min(text.length(), cursor));
        }
    }

    @Override
    public void showOutput(String text) {
        outputTextView.setText(text);
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
    public void showAddToFavorites(boolean favorite) {
        starImageView.setImageResource(favorite ? R.drawable.star_a : R.drawable.star_p);
    }

    @Override
    public String getInput() {
        return inputEditText == null ? "" : inputEditText.getEditableText().toString();
    }

    @Override
    public void showShare() {

    }

    private void initViews(){

        presenter.attach(this);
        presenter.loadTranslation();




        //There is some input logic
        subscriptions.add(RxTextView.textChanges(inputEditText)
                .debounce(1, TimeUnit.SECONDS)
                .filter(charSequence -> validateLastInput())
                .subscribe(text -> presenter.onInputChanged(text.toString()))
        );

        sourceLanguageView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SourceLanguageActivity.class);
            getActivity().startActivity(intent);
        });

        targetLanguageView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TargetLanguageActivity.class);
            getActivity().startActivity(intent);
        });

        favoriteFrame.setOnClickListener(v -> presenter.changeFavorites());

        reverseImageView.setOnClickListener(v -> presenter.reverseLanguages());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
        presenter.detach();
    }

    private boolean validateLastInput(){
        boolean filter = !ignoreInput;
        ignoreInput = false;
        return filter;
    }

}
