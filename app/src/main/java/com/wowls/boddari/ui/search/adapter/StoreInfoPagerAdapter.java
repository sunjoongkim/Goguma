package com.wowls.boddari.ui.search.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wowls.boddari.ui.search.info.InfoFragmentOne;
import com.wowls.boddari.ui.search.info.InfoFragmentThree;
import com.wowls.boddari.ui.search.info.InfoFragmentTwo;


public class StoreInfoPagerAdapter extends FragmentStatePagerAdapter
{
    public final static String LOG = "Goguma";

    private static final int PAGE_NUMBER = 3;

    private Context mContext;

    private String mStoreInfo;
    private String mStoreDistance;
    private String mStoreReview;

    public  StoreInfoPagerAdapter(FragmentManager fm, Context context, String storeInfo, String storeDistance, String storeReview)
    {
        super(fm);
        mContext = context;

        mStoreInfo = storeInfo;
        mStoreDistance = storeDistance;
        mStoreReview = storeReview;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return InfoFragmentOne.getInstance(mStoreInfo, mStoreDistance);

            case 1:
                return InfoFragmentTwo.getInstance(mStoreReview);

            case 2:
                return InfoFragmentThree.getInstance();

            default:
                return InfoFragmentOne.getInstance(mStoreInfo, mStoreDistance);
        }
    }

    @Override
    public int getCount()
    {
        return PAGE_NUMBER;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "스토어";

            case 1:
                return "리뷰";

            case 2:
                return "게시판";

            default:
                return "스토어";
        }
    }
}
