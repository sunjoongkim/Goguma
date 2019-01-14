package com.wowls.bottari.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class TestAdapter extends PagerAdapter
{
    public TestAdapter()
    {

    }
    @Override
    public int getCount()
    {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o)
    {
        return false;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        return super.instantiateItem(container, position);
    }
}
