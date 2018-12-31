package com.wowls.goguma.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.wowls.goguma.R;
import com.wowls.goguma.ui.search.SearchFragment;
import com.wowls.goguma.ui.store.StoreFragment;
import com.wowls.goguma.ui.user.UserFragment;

public class MainActivity extends FragmentActivity
{
    private static final int TAB_USER = 0;
    private static final int TAB_SEARCH = 1;
    private static final int TAB_STORE = 2;

    private TabLayout mTabLatout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTabLatout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLatout.addTab(mTabLatout.newTab().setIcon(R.drawable.ico_tab_user));
        mTabLatout.addTab(mTabLatout.newTab().setIcon(R.drawable.ico_tab_search));
        mTabLatout.addTab(mTabLatout.newTab().setIcon(R.drawable.ico_tab_store));

        mTabLatout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                switch (tab.getPosition())
                {
                    case TAB_USER:
                        Log.i("Goguma", "=================> onTabSelected TAB_USER");
                        replaceFragment(new UserFragment());
                        break;

                    case TAB_SEARCH:
                        Log.i("Goguma", "=================> onTabSelected TAB_SEARCH");
                        replaceFragment(new SearchFragment());
                        break;

                    case TAB_STORE:
                        Log.i("Goguma", "=================> onTabSelected TAB_STORE");
                        replaceFragment(new StoreFragment());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });

        mTabLatout.getTabAt(TAB_SEARCH).select();
    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).commit();
    }
}
