package com.wowls.boddari.ui.store.open;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.wowls.boddari.R;
import com.wowls.boddari.ui.store.adapter.OpenPagerAdapter;
import com.wowls.boddari.adapter.SwipeViewPager;
import com.wowls.boddari.data.MenuInfo;
import com.wowls.boddari.define.Define;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.store.StoreFragment;

import java.io.IOException;
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
    private SwipeViewPager mViewPager;
    private View mTabBlock;

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

        mViewPager = (SwipeViewPager) findViewById(R.id.view_pager_open);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout_open);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabBlock = findViewById(R.id.tab_block);
        mTabBlock.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

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

    @Override
    protected void onDestroy()
    {
        if(StoreFragment.getFragment() != null)
            StoreFragment.getFragment().initMap();

        super.onDestroy();
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
        map.put("storeName", mService.getOpenStoreName());
        map.put("storeDesc", mService.getOpenStoreDesc());
        map.put("storeLat", String.valueOf(mService.getOpenLatitude()));
        map.put("storeLon", String.valueOf(mService.getOpenLongitude()));

        Log.i(LOG, "==========================> mService.getCurrentUser() : " + mService.getCurrentUser());
        Log.i(LOG, "==========================> mService.getOpenStoreName() : " + mService.getOpenStoreName());
        Log.i(LOG, "==========================> mService.getOpenStoreDesc() : " + mService.getOpenStoreDesc());
        Log.i(LOG, "==========================> mService.getOpenLatitude() : " + mService.getOpenLatitude());
        Log.i(LOG, "==========================> mService.getOpenLongitude() : " + mService.getOpenLongitude());

        if(mRetrofitService != null)
        {
            mRetrofitService.saveStoreInfo(mService.getCurrentUser(), map).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response)
                {
                    Log.i(LOG, "register : " + response.body());

                    if(response.body() == null)
                    {
                        retryDialog("등록 실패");
                        return;
                    }

                    for(MenuInfo menu : mService.getOpenMenuList())
                        saveMenuInfo(menu);

                    mOpenStoreHandler.sendEmptyMessageDelayed(MSG_CHECK_EXIST_STORE, DELAY_CHECK_EXIST_STORE);
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
        HashMap<String, Object> map = new HashMap<>();

        map.put("storeId", mService.getCurrentUser());
        map.put("menuName", info.getMenuName());
        map.put("menuPrice", info.getMenuPrice());

        Log.i(LOG, "==========================> info.getMenuName() : " + info.getMenuName());
        Log.i(LOG, "==========================> info.getMenuPrice() : " + info.getMenuPrice());

        if(mRetrofitService != null && mService != null)
        {
            mRetrofitService.saveMenuInfo(mService.getCurrentUser(), mService.getCurrentUser(), map).enqueue(new Callback<ResponseBody>()
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

    private void checkExistStore()
    {
        if(mRetrofitService != null)
        {
            mRetrofitService.showOwnStoreList(mService.getCurrentUser()).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    ResponseBody body = response.body();

                    try
                    {
                        if(body == null)
                        {
                            retryDialog("점포 목록 가져오기 실패");
                            return;
                        }

                        mService.setExistStore(!body.string().equals("[]"));
                        finish();
                    }
                    catch (IOException e) {}
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {

                }
            });
        }
    }

    private void retryDialog(String comment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(comment)
                .setNegativeButton("확인", null)
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
                    switch (mCurrentTab)
                    {
                        case 0:
                            if(OpenFragmentOne.getFragment().isEmptyStoreName())
                                retryDialog("스토어 이름을 입력해주세요.");
                            else
                                mTabLayout.getTabAt(mCurrentTab + 1).select();
                            break;

                        case 1:
                            mTabLayout.getTabAt(mCurrentTab + 1).select();
                            break;

                        case 2:
                            if(OpenFragmentThree.getFragment().isEmptyMenuList())
                                retryDialog("메뉴를 1개이상 입력해주세요.");
                            else
                                mTabLayout.getTabAt(mCurrentTab + 1).select();
                            break;
                    }
                    break;

                case R.id.btn_check:
                    OpenFragmentFour.getFragment().saveStoreDesc();
                    saveStoreInfo();
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



    public final static int MSG_CHECK_EXIST_STORE = 1000;
    public final static int MSG_INIT_STORE_MAP = MSG_CHECK_EXIST_STORE + 1;

    private final static int DELAY_CHECK_EXIST_STORE = 500;

    private OpenStoreHandler mOpenStoreHandler = new OpenStoreHandler();

    private class OpenStoreHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {

                case MSG_CHECK_EXIST_STORE:
                    checkExistStore();
                    break;

                default:
                    break;
            }
        }
    }
}
