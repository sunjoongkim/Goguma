package com.wowls.boddari.ui.search.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wowls.boddari.ui.custom.TestStoreImageFragment;

import java.util.List;

public class TestStoreImagePagerAdapter extends FragmentStatePagerAdapter
{
    private List<TestStoreImageFragment> mImageList;

    public TestStoreImagePagerAdapter(FragmentManager fm, List<TestStoreImageFragment> list)
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
