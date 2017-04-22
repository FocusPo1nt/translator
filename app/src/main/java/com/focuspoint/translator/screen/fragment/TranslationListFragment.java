package com.focuspoint.translator.screen.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.focuspoint.translator.adapters.TranslationListAdapter;
import com.focuspoint.translator.models.Translation;
import com.focuspoint.translator.screen.TranslationListContract;
import com.focuspoint.translator.screen.Navigator;
import com.focuspoint.translator.screen.activity.MainActivity;
import com.focuspoint.translator.utils.KeyboardLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Base class for history and favorites fragments
 */

public abstract class TranslationListFragment extends Fragment implements TranslationListContract.View {
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;

    @BindView(R.id.keyboard_layout)
    protected KeyboardLayout keyboardLayout;
    @BindView(R.id.search_image_view)
    protected ImageView searchImageView;
    @BindView(R.id.search_edit_text)
    protected EditText searchEditText;

    @BindView(R.id.title)
    protected TextView title;

    @BindView(R.id.clear_history)
    protected ImageView clearHistoryImageView;


    @BindString(R.string.yes) String yes;
    @BindString(R.string.no) String no;
    @BindString(R.string.are_you_sure) String areYousure;


    protected CompositeSubscription subscriptions;
    protected TranslationListAdapter adapter;


    protected abstract TranslationListContract.Presenter getPresenter();

    protected abstract String getClearMessage();


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

    protected void initViews() {

        List<Translation> translations = new ArrayList<>();
        adapter = new TranslationListAdapter(translations, getPresenter());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        keyboardLayout.setKeyboardOpen(() -> searchEditText.setCursorVisible(true));
        keyboardLayout.setKeyboardClose(() -> searchEditText.setCursorVisible(false));

        searchEditText.addTextChangedListener(searchWatcher);

        clearHistoryImageView.setOnClickListener(v -> showClearDialog());


        //noinspection unchecked TODO generify
        getPresenter().attach(this);
        getPresenter().load();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        getPresenter().detach();
        subscriptions.unsubscribe();
    }


    @Override
    public String getSearch() {
        return searchEditText.getEditableText().toString();
    }

    @Override
    public void goTo(Navigator screen) {
        MainActivity.from(this).goToFragment(Navigator.SCREEN_TRANSLATION);
    }


    private TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            getPresenter().load();
        }
    };


    private void showClearDialog(){
        (new AlertDialog.Builder(getContext()))
                .setTitle(getClearMessage())
                .setMessage(areYousure)
                .setPositiveButton(yes, (dialog, which) -> getPresenter().clearAll())
                .setNegativeButton(no, null)
                .create().show();


    }

    @Override
    public void showError(Throwable e) {

    }
}
