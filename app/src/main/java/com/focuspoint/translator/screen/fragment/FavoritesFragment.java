package com.focuspoint.translator.screen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.focuspoint.translator.App;
import com.focuspoint.translator.R;
import com.focuspoint.translator.adapters.FavoritesAdapter;
import com.focuspoint.translator.adapters.HistoryAdapter;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.FavoriteScreenContract;
import com.focuspoint.translator.screen.HistoryScreenContract;
import com.focuspoint.translator.screen.Navigator;
import com.focuspoint.translator.screen.activity.MainActivity;
import com.focuspoint.translator.utils.KeyboardLayout;

import java.lang.annotation.RetentionPolicy;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Fragment for viewpager, that shows favorites list
 */

public class FavoritesFragment extends Fragment implements FavoriteScreenContract.View{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.keyboard_layout) KeyboardLayout keyboardLayout;
    @BindView(R.id.search_image_view) ImageView searchImageView;
    @BindView(R.id.search_edit_text) EditText searchEditText;


    @Inject
    FavoriteScreenContract.Presenter presenter;

    private CompositeSubscription subscriptions;
    private FavoritesAdapter adapter;


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
        ButterKnife.bind(this, v);

        initViews();
        return v;
    }

    private void initViews() {

        List<Translation> translations = new ArrayList<>();
        adapter = new FavoritesAdapter(translations, presenter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        keyboardLayout.setOnOpenKeyboardListener(() -> searchEditText.setCursorVisible(true));
        keyboardLayout.setOnCloseKeyboardListener(() -> searchEditText.setCursorVisible(false));

        searchEditText.addTextChangedListener(searchWatcher);

        presenter.attach(this);
        presenter.load();
    }

    public static FavoritesFragment newInstance(){
        FavoritesFragment historyFragment = new FavoritesFragment();
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
    public void showFavorites(List<Translation> translations) {
        adapter.replaceData(translations);
    }


    @Override
    public String getSearch() {
        return searchEditText.getEditableText().toString();
    }

    @Override
    public void goTo(Navigator screen) {
        MainActivity.from(this).goToFragment(screen);
    }

    private TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            presenter.load();
        }
    };
}
