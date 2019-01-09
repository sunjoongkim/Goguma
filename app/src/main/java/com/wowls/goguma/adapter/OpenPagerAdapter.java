package com.wowls.goguma.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wowls.goguma.ui.store.open.OpenFragmentFour;
import com.wowls.goguma.ui.store.open.OpenFragmentOne;
import com.wowls.goguma.ui.store.open.OpenFragmentThree;
import com.wowls.goguma.ui.store.open.OpenFragmentTwo;
import com.wowls.goguma.ui.store.open.OpenStoreActivity;

public class OpenPagerAdapter extends FragmentPagerAdapter
{
    private static final int PAGE_NUMBER = 4;

    private Context mContext;
    private OpenStoreActivity mOpenStore;

    public OpenPagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return OpenFragmentOne.getInstance();

            case 1:
                return OpenFragmentTwo.getInstance();

            case 2:
                return OpenFragmentThree.getInstance();

            case 3:
                return OpenFragmentFour.getInstance();

            default:
                return OpenFragmentOne.getInstance();
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
        return String.valueOf(position + 1);
    }
}
