package com.wowls.boddari.ui.search.info;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wowls.boddari.R;
import com.wowls.boddari.define.Define;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.custom.PageIndicator;
import com.wowls.boddari.ui.custom.TestStoreImageFragment;
import com.wowls.boddari.ui.login.LoginActivity;
import com.wowls.boddari.ui.search.adapter.StoreInfoPagerAdapter;
import com.wowls.boddari.ui.search.adapter.TestStoreImagePagerAdapter;
import com.wowls.boddari.ui.search.review.WriteReviewActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchInfoActivity extends FragmentActivity
{
    public final static String LOG = "Goguma";

    private RetrofitService mRetrofitService;

    private PageIndicator mImageIndicator;
    private ViewPager mImageViewPager;

    private StoreInfoPagerAdapter mInfoPagerAdapter;
    private TabLayout mTabLayout;
    private ViewPager mStoreViewPager;

    private ImageView mBtnBack;
    private FloatingActionButton mBtnReview;

    private GogumaService mService;

    private TextView mTextStoreName;

    private String mSelectedStoreInfo;
    private String mSelectedStoreDistance;
    private String mStoreId;

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

        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(mOnClickListener);
        mBtnReview = (FloatingActionButton) findViewById(R.id.btn_review);
        mBtnReview.setOnClickListener(mOnClickListener);

        mTextStoreName = (TextView) findViewById(R.id.text_store_name);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        mSelectedStoreInfo = intent.getStringExtra("store_info");
        mSelectedStoreDistance = intent.getStringExtra("store_distance");
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

    private void initRetrofit()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Define.URL_BASE)
                .build();

        mRetrofitService = retrofit.create(RetrofitService.class);
    }

    //test
    private void tempSetStoreImage()
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_store_image);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.sample_store_image2);

        List<TestStoreImageFragment> fragment = new ArrayList<>();

        fragment.add(new TestStoreImageFragment(this, bitmap));
        fragment.add(new TestStoreImageFragment(this, bitmap2));

        TestStoreImagePagerAdapter pagerAdapter = new TestStoreImagePagerAdapter(getSupportFragmentManager(), fragment);

        mImageIndicator = (PageIndicator) findViewById(R.id.page_indicator_store_image);
        mImageViewPager = (ViewPager) findViewById(R.id.view_pager_store_image);
        mImageViewPager.setAdapter(pagerAdapter);
        mImageIndicator.setViewPager(mImageViewPager);
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

            mStoreId = object.get(Define.KEY_STORE_ID).toString().replace("\"", "");

            showReviewListByStore(mStoreId);
        }
    }

    private void showReviewListByStore(String storeId)
    {
        if(mRetrofitService != null)
        {
            mRetrofitService.showReviewListByStore(storeId).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    try {
                        String json = response.body().string();
                        initPagerView(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {

                }
            });
        }
    }

    private void startReviewActivity()
    {
        Intent intent = new Intent(this, WriteReviewActivity.class);
        intent.putExtra("store_name", mTextStoreName.getText().toString());
        intent.putExtra("store_id", mStoreId);
        intent.putExtra("store_distance", mSelectedStoreDistance);
        startActivity(intent);
    }

    private void startLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void initPagerView(String storeReview)
    {
        mInfoPagerAdapter = new StoreInfoPagerAdapter(getSupportFragmentManager(), this, mSelectedStoreInfo, mSelectedStoreDistance, storeReview);

        mInfoPagerAdapter.notifyDataSetChanged();
        mStoreViewPager = (ViewPager) findViewById(R.id.view_pager_store_info);
        mStoreViewPager.setAdapter(mInfoPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout_store_info);
        mTabLayout.setupWithViewPager(mStoreViewPager);
        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);
        mTabLayout.getTabAt(0).select();
    }

    private void guideLoginDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("로그인후 작성 가능합니다.\n로그인 하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        startLoginActivity();
                    }
                })
                .setNegativeButton("취소", null)
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

                case R.id.btn_review:
                    if(mService != null && mService.getCurrentUser().isEmpty())
                        guideLoginDialog();
                    else
                        startReviewActivity();
                    break;
            }
        }
    };

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            mCurrentTab = tab.getPosition();

            switch (mCurrentTab)
            {
                case 0:
                    mBtnReview.hide();
                    break;

                case 1:
                    mBtnReview.show();
                    break;

                case 2:
                    mBtnReview.hide();
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab)
        {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab)
        {

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
