package com.focuspoint.translator.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.focuspoint.translator.screen.fragment.FavoritesFragment;
import com.focuspoint.translator.screen.fragment.HistoryFragment;
import com.focuspoint.translator.screen.fragment.TranslateFragment;

import java.util.List;

/**
 * Created by v_banko on 4/7/2017.
 */

public class TranslatePagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public TranslatePagerAdapter(FragmentManager fm, List<Fragment> myFrags) {
        super(fm);
        fragments = myFrags;

    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) return TranslateFragment.newInstance();
        if (position == 1) return HistoryFragment.newInstance();
        return FavoritesFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
