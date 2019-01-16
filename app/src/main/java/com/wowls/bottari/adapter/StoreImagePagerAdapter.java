package com.wowls.bottari.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wowls.bottari.ui.custom.StoreImageFragment;

import java.util.List;

public class StoreImagePagerAdapter extends FragmentStatePagerAdapter
{
    private List<StoreImageFragment> mImageList;

    public StoreImagePagerAdapter(FragmentManager fm, List<StoreImageFragment> list)
    {
        super(fm);
        mImageList = list;
    }

    @Override
    public Fragment getItem(int position)
    {
        return mImageList.get(position);
    }

    @Override
    public int getCount()
    {
        return mImageList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object)
    {
        return POSITION_NONE;
    }
}
