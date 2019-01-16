package com.wowls.bottari.ui.search.info;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wowls.bottari.R;
import com.wowls.bottari.adapter.StoreImagePagerAdapter;
import com.wowls.bottari.adapter.StoreInfoPagerAdapter;
import com.wowls.bottari.adapter.SwipeViewPager;
import com.wowls.bottari.define.Define;
import com.wowls.bottari.retrofit.RetrofitService;
import com.wowls.bottari.service.GogumaService;
import com.wowls.bottari.ui.custom.PageIndicator;
import com.wowls.bottari.ui.custom.StoreImageFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchInfoActivity extends FragmentActivity
{
    public final static String LOG = "Goguma";

    private PageIndicator mImageIndicator;
    private ViewPager mImageViewPager;

    private TabLayout mTabLayout;
    private SwipeViewPager mStoreViewPager;

    private ImageView mBtnBack;
    private ImageButton mBtnPrev, mBtnNext;
    private ImageButton mBtnComplete;

    private RetrofitService mRetrofitService;
    private GogumaService mService;

    private TextView mTextStoreName;

    private String mSelectedStoreInfo;
    private String mSelectedStoreDistance;

    private int mCurrentTab = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_store_info_main);

        mService = GogumaService.getService();

        initRetrofit();

        Intent intent = getIntent();
        mSelectedStoreInfo = intent.getStringExtra("store_info");
        mSelectedStoreDistance = intent.getStringExtra("store_distance");

        // test
        tempSetStoreImage();

        StoreInfoPagerAdapter infoPagerAdapter = new StoreInfoPagerAdapter(getSupportFragmentManager(), this, mSelectedStoreInfo, mSelectedStoreDistance);

        mStoreViewPager = (SwipeViewPager) findViewById(R.id.view_pager_store_info);
        mStoreViewPager.setAdapter(infoPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout_store_info);
        mTabLayout.setupWithViewPager(mStoreViewPager);
        mTabLayout.getTabAt(0).select();

        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(mOnClickListener);

        mTextStoreName = (TextView) findViewById(R.id.text_store_name);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        storeParser(mSelectedStoreInfo);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    //test
    private void tempSetStoreImage()
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_store_image);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.sample_store_image2);

        List<StoreImageFragment> fragment = new ArrayList<>();

        fragment.add(new StoreImageFragment(this, bitmap));
        fragment.add(new StoreImageFragment(this, bitmap2));

        StoreImagePagerAdapter pagerAdapter = new StoreImagePagerAdapter(getSupportFragmentManager(), fragment);

        mImageIndicator = (PageIndicator) findViewById(R.id.page_indicator_store_image);
        mImageViewPager = (ViewPager) findViewById(R.id.view_pager_store_image);
        mImageViewPager.setAdapter(pagerAdapter);
        mImageViewPager.setOnPageChangeListener(mOnPageChangeListener);
        mImageIndicator.setViewPager(mImageViewPager);
    }

    private void initRetrofit()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Define.URL_BASE)
                .build();

        mRetrofitService = retrofit.create(RetrofitService.class);
    }

    private void storeParser(String json)
    {
        String storeInfo;

        JsonParser parser = new JsonParser();
        JsonElement element = (JsonElement) parser.parse(json);

        if(!element.isJsonNull())
        {
            JsonObject object = element.getAsJsonObject();

            storeInfo = object.get("storeInfo").toString();
            storeInfoParser(storeInfo);
        }
    }

    private void storeInfoParser(String json)
    {
        JsonParser parser = new JsonParser();
        JsonElement element = (JsonElement) parser.parse(json);

        if(!element.isJsonNull())
        {
            JsonObject object = element.getAsJsonObject();

            if(object.get(Define.KEY_STORE_NAME) != null)
                mTextStoreName.setText(object.get(Define.KEY_STORE_NAME).toString().replace("\"", ""));
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

                    break;

                case 1:
                case 2:

                    break;

                case 3:

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
                    break;

                default:
                    break;
            }
        }
    }
}
