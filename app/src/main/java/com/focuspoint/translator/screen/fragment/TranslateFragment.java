package com.focuspoint.translator.screen.fragment;

import android.content.Context;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by v_banko on 3/31/2017.
 */

public class TranslateFragment extends Fragment{


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_translation, container, false);
        ButterKnife.bind(this,v);

        toolbar.inflateMenu(R.menu.menu_language_choose);
        Toast.makeText(getContext(), "" + ((TextView)toolbar.findViewById(R.id.first_language)).getText().toString(), Toast.LENGTH_SHORT).show();
        return v;
    }

    public static TranslateFragment newInstance(){
        TranslateFragment translateFragment = new TranslateFragment();
        return translateFragment;
    }


}
