package com.wowls.boddari.ui.search.review;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.wowls.boddari.BoddariApplication;
import com.wowls.boddari.R;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.search.info.SearchInfoActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteReviewActivity extends Activity
{
    public final static String LOG = "Goguma";

    private RetrofitService mRetrofitService;
    private GogumaService mService;

    private TextView mTextStoreName;
    private SimpleRatingBar mRatingBar;
    private EditText mEditReview;
    private ImageView mBtnBack;
    private TextView mBtnComplete;

    private String mStoreId;
    private String mStoreInfo;
    private String mStoreDistance;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_store_info_review);

        mRetrofitService = BoddariApplication.getInstance().getRetrofitService();
        mService = GogumaService.getService();

        Intent intent = getIntent();
        String storeName = intent.getStringExtra("store_name");
        mStoreId = intent.getStringExtra("store_id");
        mStoreDistance = intent.getStringExtra("store_distance");

        mTextStoreName = (TextView) findViewById(R.id.text_store_name);
        mTextStoreName.setText(storeName);

        mRatingBar = (SimpleRatingBar) findViewById(R.id.rating_bar);
        mEditReview = (EditText) findViewById(R.id.edit_review);
        mEditReview.requestFocus();

        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(mOnClickListener);
        mBtnComplete = (TextView) findViewById(R.id.btn_complete);
        mBtnComplete.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
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

                case R.id.btn_complete:
                    if(mEditReview.getText().toString().isEmpty())
                        retryDialog("리뷰 내용을 작성해주세요.");
                    else
                        saveReview();
                    break;
            }
        }
    };

    private void saveReview()
    {
        if(mService != null)
        {
            Map<String, String> review = new HashMap<>();
            review.put("writerId", mService.getCurrentUser());
            review.put("storeId", mStoreId);
            review.put("rating", String.valueOf((int) mRatingBar.getRating()));
            review.put("comment", mEditReview.getText().toString());

            if(mRetrofitService != null)
            {
                mRetrofitService.saveReview(mService.getCurrentUser(), review).enqueue(new Callback<ResponseBody>()
                {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        Log.i(LOG, "===========> body : " + response.body());
                        if(response.body() != null)
                            getStoreInfo();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t)
                    {

                    }
                });
            }
        }

    }

    private void getStoreInfo()
    {
        if(mRetrofitService != null)
        {
            mRetrofitService.showStoreInfo(mStoreId).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    if(response.body() == null)
                        return;

                    try {
                        mStoreInfo = response.body().string();
                        startInfoActivity();
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

    private void startInfoActivity()
    {
        Intent intent = new Intent(this, SearchInfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("store_info", mStoreInfo);
        intent.putExtra("store_distance", mStoreDistance);
        startActivity(intent);

        finish();
    }

    private void retryDialog(String comment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(comment)
                .setNegativeButton("다시 시도", null)
                .create()
                .show();
    }
}
