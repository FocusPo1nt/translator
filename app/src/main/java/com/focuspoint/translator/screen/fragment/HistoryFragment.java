package com.focuspoint.translator.screen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.focuspoint.translator.App;
import com.focuspoint.translator.R;
import com.focuspoint.translator.adapters.HistoryAdapter;
import com.focuspoint.translator.database.DB;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.HistoryScreenContract;
import com.focuspoint.translator.screen.Navigator;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.annotation.RetentionPolicy;
import java.security.cert.TrustAnchor;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Fragment for viewpager, that shows translation history list
 */

public class HistoryFragment extends Fragment implements HistoryScreenContract.View{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;


    @Inject
    HistoryScreenContract.Presenter presenter;

    private CompositeSubscription subscriptions;
    private HistoryAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        App.from(getContext()).getComponent().inject(this);
        subscriptions = new CompositeSubscription();
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this,v);

        initViews();
        return v;
    }

    private void initViews() {

        List <Translation> translations = new ArrayList<>();
        adapter = new HistoryAdapter(translations, presenter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        presenter.attach(this);
        presenter.load();
    }

    public static HistoryFragment newInstance(){
        HistoryFragment historyFragment = new HistoryFragment();
        return historyFragment;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detach();
        subscriptions.unsubscribe();
    }


    @Override
    public void showError(Throwable e) {

    }

    @Override
    public void showHistory(List<Translation> translations) {
        adapter.replaceData(translations);
    }


    @Override
    public String getSearch() {
        return null;
    }

    @Override
    public void goTo(Navigator screen) {

    }
}
