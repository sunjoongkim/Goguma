package com.wowls.boddari.ui.store.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wowls.boddari.ui.custom.StoreImageFragment;
import com.wowls.boddari.ui.store.AddImageFragment;

import java.util.List;

public class StoreManagerImageAdapter extends FragmentStatePagerAdapter
{
    private List<StoreImageFragment> mImageList;

    public StoreManagerImageAdapter(FragmentManager fm, List<StoreImageFragment> list)
    {
        super(fm);
        mImageList = list;
    }

    @Override
    public Fragment getItem(int position)
    {
        if(position == mImageList.size())
            return AddImageFragment.getInstance();

        return mImageList.get(position);
    }

    @Override
    public int getCount()
    {
        return mImageList.size() + 1;
    }

    @Override
    public int getItemPosition(@NonNull Object object)
    {
        return POSITION_NONE;
    }
}
