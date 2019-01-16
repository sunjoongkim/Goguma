package com.wowls.bottari.ui.etc.user.regist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.wowls.bottari.R;
import com.wowls.bottari.adapter.RegistPagerAdapter;
import com.wowls.bottari.adapter.SwipeViewPager;
import com.wowls.bottari.ui.custom.PageIndicator;


public class EtcUserRegistActivity extends FragmentActivity
{
    public final static String LOG = "Goguma";

    private Context mContext;

    private TabLayout mTabLayout;
    private SwipeViewPager mViewPager;
    private static PageIndicator mPageIndicator;

    private ImageView mBtnBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etc_user_regist_main);

        RegistPagerAdapter pagerAdapter = new RegistPagerAdapter(getSupportFragmentManager());

        mViewPager = (SwipeViewPager) findViewById(R.id.view_pager_regist);
        mPageIndicator = (PageIndicator) findViewById(R.id.page_indicator);
        mViewPager.setAdapter(pagerAdapter);
        mPageIndicator.setViewPager(mViewPager);

        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    public static void selectTab(int tab)
    {
        mPageIndicator.setCurrentItem(tab);
    }

}
