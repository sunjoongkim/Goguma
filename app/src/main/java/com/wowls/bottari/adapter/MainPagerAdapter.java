package com.wowls.bottari.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.wowls.bottari.R;
import com.wowls.bottari.ui.search.SearchFragment;
import com.wowls.bottari.ui.store.StoreFragment;
import com.wowls.bottari.ui.etc.EtcFragment;

public class MainPagerAdapter extends FragmentPagerAdapter
{
    private static final int PAGE_NUMBER = 3;

    private static final int TAB_STORE = 0;
    private static final int TAB_SEARCH = 1;
    private static final int TAB_ETC = 2;

    private Context mContext;

    public MainPagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case TAB_ETC:
                return EtcFragment.getInstance();

            case TAB_SEARCH:
                return SearchFragment.getInstance();

            case TAB_STORE:
                return StoreFragment.getInstance();

            default:
                return SearchFragment.getInstance();
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
        int drawable = 0;

        switch (position)
        {
            case TAB_ETC:
                drawable = R.drawable.ico_tab_etc;
                break;

            case TAB_SEARCH:
                drawable = R.drawable.ico_tab_search;
                break;

            case TAB_STORE:
                drawable = R.drawable.ico_tab_store;
                break;
        }

        Drawable image = mContext.getResources().getDrawable(drawable);
        image.setBounds(0, 0, 85, 85);
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
