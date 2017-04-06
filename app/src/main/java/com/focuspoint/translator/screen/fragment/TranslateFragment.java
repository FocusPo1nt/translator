package com.focuspoint.translator.screen.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.focuspoint.translator.R;
import com.focuspoint.translator.screen.activity.LanguageActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main view, where you can translate your sentence;
 */

public class TranslateFragment extends Fragment{


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.from_language) TextView fromTextView;
    @BindView(R.id.to_language) TextView toTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_translation, container, false);
        ButterKnife.bind(this, v);

        fromTextView.setOnClickListener(v1 -> {
            Intent intent = new Intent(getContext(), LanguageActivity.class);
            getActivity().startActivity(intent);
        });
        return v;
    }

    public static TranslateFragment newInstance(){
        return new TranslateFragment();
    }


}
