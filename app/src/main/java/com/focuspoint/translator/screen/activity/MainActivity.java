package com.focuspoint.translator.screen.activity;

import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.focuspoint.translator.App;
import com.focuspoint.translator.R;
import com.focuspoint.translator.adapters.TranslatePagerAdapter;
import com.focuspoint.translator.presenters.TranslationPresenter;
import com.focuspoint.translator.screen.Navigator;
import com.focuspoint.translator.screen.TranslationScreenContract;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @Inject TranslationScreenContract.Presenter translationPresenter;

    private Unbinder unbinder;
    private FragmentPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        App.from(this).getComponent().inject(this);
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

        pagerAdapter = new TranslatePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        tabLayout.setupWithViewPager(viewPager, true);
        tabLayout.getTabAt(0).setIcon(R.drawable.translate);
        tabLayout.getTabAt(1).setIcon(R.drawable.history);
        tabLayout.getTabAt(2).setIcon(R.drawable.star);


        //Automatically unregistering the event on the Activity's onDestroy
        KeyboardVisibilityEvent.setEventListener(this,
                isOpen -> {
                    if (viewPager != null && viewPager.getCurrentItem() == 0 && !isOpen){
                        translationPresenter.onKeyboardClose();
                    }
                });
    }




    /**Move to screen using Navigator enum*/
    public void goToFragment(Navigator screen){
        if (screen == Navigator.SCREEN_TRANSLATION){
            viewPager.setCurrentItem(0, true);
        }
    }

    /**Obtain MainActivity instance from Fragment*/
    public static MainActivity from(Fragment fragment){
        return (MainActivity) fragment.getActivity();
    }

    //return to translation screen if '<' pressed;
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0){
            viewPager.setCurrentItem(0);
        }else {
            super.onBackPressed();
        }
    }
}
