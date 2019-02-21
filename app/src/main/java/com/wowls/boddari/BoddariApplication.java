package com.wowls.boddari;

import android.app.Application;

import com.kakao.auth.KakaoSDK;
import com.wowls.boddari.adapter.login.KaKaoSDKAdapter;
import com.wowls.boddari.define.Define;
import com.wowls.boddari.retrofit.RetrofitService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BoddariApplication extends Application
{
    private RetrofitService mRetrofitService;

    private static BoddariApplication mApplication;

    public static BoddariApplication getInstance()
    {
        return mApplication;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        mApplication = this;
        initRetrofit();

        KakaoSDK.init(new KaKaoSDKAdapter());
    }

    private void initRetrofit()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Define.URL_BASE)
                .build();

        mRetrofitService = retrofit.create(RetrofitService.class);
    }

    public RetrofitService getRetrofitService()
    {
        return mRetrofitService;
    }
}
