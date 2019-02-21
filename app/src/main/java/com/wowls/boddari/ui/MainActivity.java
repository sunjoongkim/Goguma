package com.wowls.boddari.ui;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.wowls.boddari.R;
import com.wowls.boddari.adapter.MainPagerAdapter;
import com.wowls.boddari.ui.etc.EtcFragment;

public class MainActivity extends FragmentActivity
{
    public final static String LOG = "Goguma";

    private static final int TAB_STORE = 0;
    private static final int TAB_SEARCH = 1;
    private static final int TAB_ETC = 2;

    private LocationManager mLocationManager;

    private static TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.i(LOG, "=====================> CustomGalleryActivity onCreate ");
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(pagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        mTabLayout.getTabAt(TAB_SEARCH).select();

        checkGpsState();
    }

    @Override
    protected void onDestroy()
    {
        Log.i(LOG, "=====================> CustomGalleryActivity onDestroy ");
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        if(mTabLayout.getSelectedTabPosition() == TAB_SEARCH)
            super.onBackPressed();
        else
            mTabLayout.getTabAt(TAB_SEARCH).select();
    }

    // GPS on/off 체크
    private void checkGpsState()
    {
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
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
                    break;

                case TAB_SEARCH:
                    break;

                case TAB_ETC:
                    if(EtcFragment.getFragment() != null)
                        EtcFragment.getFragment().checkLogin();
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
                    break;

                case TAB_SEARCH:
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
//                    if(SearchFragment.getFragment() != null)
//                        SearchFragment.getFragment().initMap();
                    break;

                case MSG_INIT_STORE_MAP:
//                    if(StoreFragment.getFragment() != null)
//                        StoreFragment.getFragment().initMap();
                    break;

                default:
                    break;
            }
        }
    }
}