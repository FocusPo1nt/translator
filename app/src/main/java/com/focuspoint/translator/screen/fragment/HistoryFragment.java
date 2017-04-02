package com.focuspoint.translator.screen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.focuspoint.translator.R;

import java.lang.annotation.RetentionPolicy;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by v_banko on 3/31/2017.
 */

public class HistoryFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_translation, container, false);

        return v;
    }

    public static TranslateFragment newInstance(){
        TranslateFragment translateFragment = new TranslateFragment();
        return translateFragment;
    }
}
