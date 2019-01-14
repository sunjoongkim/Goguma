package com.wowls.bottari.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import com.nhn.android.maps.maplib.NGeoPoint;
import com.wowls.bottari.R;
import com.wowls.bottari.data.StoreInfo;
import com.wowls.bottari.define.Define;
import com.wowls.bottari.ui.search.SearchFragment;
import com.wowls.bottari.ui.search.pager.SearchPagerFragment;
import com.wowls.bottari.ui.search.pager.SearchPagerLayout;

import java.util.ArrayList;


public class SearchPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener
{
    private static final String LOG = "Goguma";

    private final static int PAGE_SCROLL_STATE_END = 0;

    private SearchFragment mSearchFragment;
    private FragmentManager mFragmentManager;
    private ArrayList<StoreInfo> mStoreList;
    private int mPageCount;
    private float mScale = 0f;
    private boolean mIsSelectionByUser = false;

    private int mCountDir = 0;

    public SearchPagerAdapter(SearchFragment fragment, FragmentManager manager, ArrayList<StoreInfo> list)
    {
        super(manager);

        mSearchFragment = fragment;
        mFragmentManager = manager;
        mStoreList = list;

        mPageCount = mStoreList.size();
        SearchPagerFragment.setPageViewStoreInfo(mStoreList);
    }

    @Override
    public Fragment getItem(int position)
    {
//        Log.i(LOG, "====================? getItem : " + position);
//        Log.i(LOG, "====================? mPageCount : " + mPageCount);

        try
        {
//            if(position == Define.CURRENT_IMAGE)
//                mScale = Define.BIG_SCALE;
//            else

            mScale = Define.SMALL_SCALE;

            position = position % mPageCount;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return SearchPagerFragment.getInstance(mSearchFragment.getContext(), position, mScale);
    }

    @Override
    public int getCount()
    {
        return mPageCount;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        return super.instantiateItem(container, position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
        try
        {
            if (positionOffset >= 0f && positionOffset <= 1f)
            {
                SearchPagerLayout current = getRootView(position);
                current.setScaleBoth(Define.BIG_SCALE - Define.DIFF_SCALE * positionOffset);
//                current.setAlpha(0.5f);

//                mSearchFragment.setEnableOutline(false);

                if(positionOffset == 0f && positionOffsetPixels == 0)
                {
//                    current.setAlpha(1.0f);
//                    mSearchFragment.setEnableOutline(true);
                }

                if(mPageCount - 1 >= position + 1)
                {
                    SearchPagerLayout next = getRootView(position + 1);
                    next.setScaleBoth(Define.SMALL_SCALE + Define.DIFF_SCALE * positionOffset);
//                    next.setAlpha(0.5f);
                }

                if(0 <= position - 1)
                {
                    SearchPagerLayout prev = getRootView(position - 1);
                    prev.setScaleBoth(Define.SMALL_SCALE + Define.DIFF_SCALE * positionOffset);
//                    prev.setAlpha(0.5f);
                }
            }
        }
        catch (Exception e)
        {
            Log.i(LOG, "onPageScrolled Exception : " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onPageSelected(int position)
    {
//        Log.i(LOG, "onPageSelected position : " + position);

        mSearchFragment.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
//        Log.i(LOG, "onPageScrollStateChanged :" + state);
        mIsSelectionByUser = true;

        if(state == PAGE_SCROLL_STATE_END)
            mIsSelectionByUser = false;
    }

    @SuppressWarnings("ConstantConditions")
    private SearchPagerLayout getRootView(int position)
    {
        return (SearchPagerLayout) mFragmentManager.findFragmentByTag(getFragmentTag(position)).getView().findViewById(R.id.pager_layout);
    }

    private String getFragmentTag(int position)
    {
        return "android:switcher:" + mSearchFragment.getViewPager().getId() + ":" + position;
    }

}
