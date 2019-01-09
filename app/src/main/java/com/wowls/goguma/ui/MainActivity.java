package com.wowls.goguma.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.wowls.goguma.R;
import com.wowls.goguma.adapter.MainPagerAdapter;
import com.wowls.goguma.ui.search.SearchFragment;
import com.wowls.goguma.ui.store.StoreFragment;

public class MainActivity extends FragmentActivity
{
    private static final int TAB_STORE = 0;
    private static final int TAB_SEARCH = 1;
    private static final int TAB_ETC = 2;

    private static TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(pagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        mTabLayout.getTabAt(TAB_SEARCH).select();
    }

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            Log.i("Goguma", "==============> onTabSelected : " + tab.getPosition());
            switch (tab.getPosition())
            {
                case TAB_STORE:
                    mMainHandler.sendEmptyMessageDelayed(MSG_INIT_STORE_MAP, 0);
                    break;

                case TAB_SEARCH:
                    mMainHandler.sendEmptyMessageDelayed(MSG_INIT_SEARCH_MAP, 0);
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab)
        {
            Log.i("Goguma", "==============> onTabUnselected : " + tab.getPosition());
            switch (tab.getPosition())
            {
                case TAB_STORE:
                    if(StoreFragment.getFragment() != null)
                        StoreFragment.getFragment().finishMap();
                    break;

                case TAB_SEARCH:
                    if(SearchFragment.getFragment() != null)
                        SearchFragment.getFragment().finishMap();
                    break;
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab)
        {

        }
    };

    public static void selectTab(int tab)
    {
        mTabLayout.getTabAt(tab).select();
    }

    public final static int MSG_INIT_SEARCH_MAP = 1000;
    public final static int MSG_INIT_STORE_MAP = MSG_INIT_SEARCH_MAP + 1;

    private final static int DELAY_CHECK_SERVICE_STARTED = 100;
    private final static int DELAY_LOADING_COMPLETED = 1500;

    private MainHanaler mMainHandler = new MainHanaler();

    private class MainHanaler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {

                case MSG_INIT_SEARCH_MAP:
                    if(SearchFragment.getFragment() != null)
                        SearchFragment.getFragment().initMap();
                    break;

                case MSG_INIT_STORE_MAP:
                    if(StoreFragment.getFragment() != null)
                        StoreFragment.getFragment().initMap();
                    break;

                default:
                    break;
            }
        }
    }
}
