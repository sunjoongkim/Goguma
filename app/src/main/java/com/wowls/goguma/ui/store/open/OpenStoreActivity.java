package com.wowls.goguma.ui.store.open;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.wowls.goguma.R;
import com.wowls.goguma.adapter.OpenPagerAdapter;
import com.wowls.goguma.data.MenuInfo;
import com.wowls.goguma.define.Define;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.service.GogumaService;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenStoreActivity extends FragmentActivity
{
    public final static String LOG = "Goguma";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ImageView mBtnBack;
    private ImageButton mBtnPrev, mBtnNext;
    private ImageButton mBtnComplete;

    private RetrofitService mRetrofitService;
    private GogumaService mService;

    private int mCurrentTab = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_open_main);

        mService = GogumaService.getService();

        initRetrofit();
        OpenPagerAdapter pagerAdapter = new OpenPagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) findViewById(R.id.view_pager_open);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout_open);
        mTabLayout.setupWithViewPager(mViewPager);

        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(mOnClickListener);
        mBtnPrev = (ImageButton) findViewById(R.id.btn_prev);
        mBtnPrev.setOnClickListener(mOnClickListener);
        mBtnNext = (ImageButton) findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(mOnClickListener);
        mBtnComplete = (ImageButton) findViewById(R.id.btn_check);
        mBtnComplete.setOnClickListener(mOnClickListener);

        mTabLayout.getTabAt(0).select();
    }

    private void initRetrofit()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Define.URL_BASE)
                .build();

        mRetrofitService = retrofit.create(RetrofitService.class);
    }

    private void saveStoreInfo()
    {
        HashMap<String, String> map = new HashMap<>();

        map.put("storeId", mService.getCurrentUser());
        map.put("ownerId", mService.getCurrentUser());
        map.put("storeName", mService.getStoreName());
        map.put("storeDesc", mService.getStoreDesc());
        map.put("storeLat", String.valueOf(mService.getLatitude()));
        map.put("storeLon", String.valueOf(mService.getLongitude()));

        if(mRetrofitService != null)
        {
            mRetrofitService.saveStoreInfo(mService.getCurrentUser(), map).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response)
                {
                    Log.i(LOG, "register : " + response.body());

                    if(response.body() == null)
                        retryDialog("위치 등록 실패");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Log.i(LOG, "onFailure : " + t.toString());
                }
            });
        }
    }

    public void saveMenuInfo(MenuInfo info)
    {
        HashMap<String, String> map = new HashMap<>();
        map.put(Define.KEY_MENU_NAME, info.getMenuName());
        map.put(Define.KEY_MENU_PRICE, info.getMenuPrice());

        if(mRetrofitService != null && mService != null)
        {
            mRetrofitService.saveMenuInfo(mService.getCurrentUser(), map).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    Log.i(LOG, "register : " + response.body());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Log.i(LOG, "onFailure : " + t.toString());
                }
            });
        }
    }

    private void retryDialog(String comment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(comment)
                .setNegativeButton("다시 시도", null)
                .create()
                .show();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_prev:
                    mTabLayout.getTabAt(mCurrentTab - 1).select();
                    break;

                case R.id.btn_next:
                    mTabLayout.getTabAt(mCurrentTab + 1).select();
                    break;

                case R.id.btn_check:
                    saveStoreInfo();

                    for(MenuInfo menu : mService.getMenuList())
                        saveMenuInfo(menu);

                    finish();
                    break;

                case R.id.btn_back:
                    finish();
                    break;
            }
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int i, float v, int i1)
        {

        }

        @Override
        public void onPageSelected(int position)
        {
            mCurrentTab = position;

            switch (position)
            {
                case 0:
                    mBtnPrev.setVisibility(View.INVISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);
                    mBtnComplete.setVisibility(View.INVISIBLE);
                    break;

                case 1:
                case 2:
                    mBtnPrev.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);
                    mBtnComplete.setVisibility(View.INVISIBLE);
                    break;

                case 3:
                    mBtnPrev.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.INVISIBLE);
                    mBtnComplete.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int i)
        {

        }
    };
}
