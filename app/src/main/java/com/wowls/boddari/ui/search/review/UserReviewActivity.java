package com.wowls.boddari.ui.search.review;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wowls.boddari.BoddariApplication;
import com.wowls.boddari.R;
import com.wowls.boddari.data.UserReviewInfo;
import com.wowls.boddari.define.Define;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.search.adapter.UserReviewAdapter;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReviewActivity extends Activity
{
    public final static String LOG = "Goguma";

    private RetrofitService mRetrofitService;
    private GogumaService mService;

    private ArrayList<UserReviewInfo> mReviewList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private TextView mTextUserName;
    private ImageView mImageProfile;
    private TextView mReviewCount;
    private ImageView mBtnBack;

    private String mUserId;
    private String mStoreInfo;
    private String mStoreDistance;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_store_info_user_review);

        mRetrofitService = BoddariApplication.getInstance().getRetrofitService();
        mService = GogumaService.getService();

        Intent intent = getIntent();
        mUserId = intent.getStringExtra("user_id");

        mRecyclerView = (RecyclerView) findViewById(R.id.review_list);
        mTextUserName = (TextView) findViewById(R.id.text_user_name);
        mTextUserName.setText(mUserId + "님의 리뷰 모아보기");
        mImageProfile = (ImageView) findViewById(R.id.image_profile);
        mReviewCount = (TextView) findViewById(R.id.text_review_count);

        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(mOnClickListener);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        getReviewList();
        getProfileImage();
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

    private void getProfileImage()
    {
        Glide.with(this)
             .load(R.drawable.bg_profile)
             .apply(new RequestOptions().circleCrop())
             .into(mImageProfile);
    }

    private void getReviewList()
    {
        if(mRetrofitService != null)
        {
            mRetrofitService.showReviewListByWriter(mUserId).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    Log.i(LOG, "===========> body : " + response.body());
                    if(response.body() == null)
                        return;

                    try {
                        reviewParser(response.body().string());
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


    private void reviewParser(String json)
    {
        Log.e(LOG, "========================> reviewParser json : " + json);
        ArrayList<UserReviewInfo> reviewList = new ArrayList<>();

        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        mReviewCount.setText("작성한 리뷰 " + array.size());

        String storeName;
        float rating;
        String comment;
        String updDate;

        for(JsonElement element : array)
        {
            if(element.isJsonNull())
                return;

            JsonObject object = element.getAsJsonObject();

            storeName = object.get(Define.KEY_STORE_NAME).toString().replace("\"", "");
            rating = Float.parseFloat(object.get(Define.KEY_RATING).toString()) / 2f;
            updDate = object.get(Define.KEY_UPD_DATE).toString().replace("\"", "");
            comment = object.get(Define.KEY_COMMENT).toString().replace("\"", "");

            UserReviewInfo reviewInfo = new UserReviewInfo(storeName, rating, comment, updDate);
            reviewList.add(reviewInfo);
        }

        mRecyclerView.setAdapter(new UserReviewAdapter(reviewList));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration()
        {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
            {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 100;
            }
        });
    }

}
