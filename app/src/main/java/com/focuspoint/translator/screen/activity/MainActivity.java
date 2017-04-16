package com.focuspoint.translator.screen.activity;

import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.focuspoint.translator.App;
import com.focuspoint.translator.R;
import com.focuspoint.translator.adapters.TranslatePagerAdapter;
import com.focuspoint.translator.models.Language;
import com.focuspoint.translator.screen.TranslationScreenContract;
import com.focuspoint.translator.screen.fragment.FavoritesFragment;
import com.focuspoint.translator.screen.fragment.HistoryFragment;
import com.focuspoint.translator.screen.fragment.TranslateFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {

    @Inject Retrofit retrofit;



    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tab_layout) TabLayout tabLayout;

    private Unbinder unbinder;
    private FragmentPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void initViews(){
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        App.from(this).getComponent().inject(this);

        pagerAdapter = new TranslatePagerAdapter(getSupportFragmentManager(), new ArrayList<>());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        tabLayout.setupWithViewPager(viewPager, true);
        tabLayout.getTabAt(0).setIcon(R.drawable.translate);
        tabLayout.getTabAt(1).setIcon(R.drawable.history);
        tabLayout.getTabAt(2).setIcon(R.drawable.star);
    }

}
