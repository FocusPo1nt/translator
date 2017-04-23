package com.focuspoint.translator.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.focuspoint.translator.screen.fragment.FavoritesFragment;
import com.focuspoint.translator.screen.fragment.HistoryFragment;
import com.focuspoint.translator.screen.fragment.TranslateFragment;


public class TranslatePagerAdapter extends FragmentPagerAdapter {

    public TranslatePagerAdapter(FragmentManager fm) {
        super(fm);
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
