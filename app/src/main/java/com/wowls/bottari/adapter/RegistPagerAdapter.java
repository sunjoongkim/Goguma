package com.wowls.bottari.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wowls.bottari.ui.etc.user.regist.RegistFragmentOne;
import com.wowls.bottari.ui.etc.user.regist.RegistFragmentTwo;

public class RegistPagerAdapter extends FragmentStatePagerAdapter
{
    private static final int PAGE_NUMBER = 2;

    public RegistPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return RegistFragmentOne.getInstance();

            case 1:
                return RegistFragmentTwo.getInstance();

            default:
                return RegistFragmentOne.getInstance();
        }
    }

    @Override
    public int getCount()
    {
        return PAGE_NUMBER;
    }

    @Override
    public int getItemPosition(@NonNull Object object)
    {
        return POSITION_NONE;
    }
}
