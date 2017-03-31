package com.focuspoint.translator;

import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.focuspoint.translator.screen.TranslateFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tab_layout) TabLayout tabLayout;

    private Unbinder unbinder;
    private FragmentPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
        App.from(this).getComponent().inject(this);

        pagerAdapter = new TranslatePagerAdapter(getSupportFragmentManager(), new ArrayList<>());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager, true);
        tabLayout.getTabAt(0).setIcon(R.drawable.search_black);
        tabLayout.getTabAt(1).setIcon(R.drawable.menu);

    }

    class TranslatePagerAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragments;

        public TranslatePagerAdapter(FragmentManager fm, List<Fragment> myFrags) {
            super(fm);
            fragments = myFrags;
        }

        @Override
        public Fragment getItem(int position) {
            return TranslateFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
