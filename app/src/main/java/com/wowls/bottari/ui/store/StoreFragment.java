package com.wowls.bottari.ui.store;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wowls.bottari.R;
import com.wowls.bottari.data.MenuInfo;
import com.wowls.bottari.define.Define;
import com.wowls.bottari.retrofit.RetrofitService;
import com.wowls.bottari.service.GogumaService;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoreFragment extends Fragment
{
    private static final String LOG = "Goguma";

    private static StoreFragment mMyFragment;
    private Context mContext;

    private GuideLoginView mGuideLoginView;
    private GuideOpenView mGuideOpenView;
    private StoreManagerView mStoreManagerView;
    private RegistMenuView mRegistMenuView;

    private GogumaService mService;
    private RetrofitService mRetrofitService;

    public static StoreFragment getInstance()
    {
        Bundle args = new Bundle();

        StoreFragment fragment = new StoreFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static StoreFragment getFragment()
    {
        return mMyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Log.i(LOG, "==========================> StoreFragment onCreateView");
        View view = inflater.inflate(R.layout.store_main, container, false);

        mMyFragment = this;

        initRetrofit();
        mContext = getContext();
        mService = GogumaService.getService();

        mGuideLoginView = new GuideLoginView(mContext, view.findViewById(R.id.guide_login_view));
        mGuideOpenView = new GuideOpenView(mContext, view.findViewById(R.id.guide_open_view));
        mStoreManagerView = new StoreManagerView(mContext, getActivity(), view.findViewById(R.id.store_manage_view), mRetrofitService);

//        mRegistMenuView = new RegistMenuView(mContext, view.findViewById(R.id.regist_menu_view), mRetrofitService);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        setServiceToView();
        checkLogin();
    }

    @Override
    public void onDestroy()
    {
        mMyFragment = null;
        super.onDestroy();
    }

    public void initMap()
    {
        if(mStoreManagerView != null)
            mStoreManagerView.initMap();
    }

    public void finishMap()
    {
        if(mStoreManagerView != null)
            mStoreManagerView.finishMap();
    }

    private void initRetrofit()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Define.URL_BASE)
                .build();

        mRetrofitService = retrofit.create(RetrofitService.class);
    }

    private void checkLogin()
    {
        if(mService != null)
        {
            clearView();

            if(mService.getCurrentUser().isEmpty())
                mGuideLoginView.setVisible(true);
            else if(!mService.isExistStore())
                mGuideOpenView.setVisible(true);
            else
            {
                mStoreManagerView.setVisible(true);
                getStoreList();
                getMenuList();
            }
        }
    }

    private void getStoreList()
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

                        storeParser(body.string());
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

    private void getMenuList()
    {
        if(mRetrofitService != null)
        {
            mRetrofitService.showOwnMenuList(mService.getCurrentUser()).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response)
                {
                    ResponseBody body = response.body();

                    try
                    {
                        if(body == null)
                        {
                            retryDialog("메뉴 목록 가져오기 실패");
                            return;
                        }

                        menuParser(body.string());
                    }
                    catch (IOException e) {}
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Log.i(LOG, "onFailure : " + t.toString());
                }
            });
        }
    }

    private void storeParser(String json)
    {
        Log.i(LOG, "====================> storeParser : " + json);

        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        for(JsonElement element : array)
        {
            JsonObject object = element.getAsJsonObject();

            if(mStoreManagerView != null)
            {
                if(object.get(Define.KEY_STORE_NAME) == null)
                    mStoreManagerView.setTextStoreName(mService.getCurrentUser() + " 의 스토어");
                else
                    mStoreManagerView.setTextStoreName(object.get(Define.KEY_STORE_NAME).toString().replace("\"", ""));

                if(object.get(Define.KEY_STORE_DESC) == null)
                    mStoreManagerView.setTextStoreDesc("");
                else
                    mStoreManagerView.setTextStoreDesc(object.get(Define.KEY_STORE_DESC).toString().replace("\"", ""));

                mStoreManagerView.setStoreLongitude(object.get(Define.KEY_STORE_LON).toString());
                mStoreManagerView.setStoreLatitude(object.get(Define.KEY_STORE_LAT).toString());
            }
        }
    }

    private void menuParser(String json)
    {
        Log.i(LOG, "=============> showOwnMenuList : " + json);

        ArrayList<MenuInfo> menuList = new ArrayList<>();

        String menuName;
        String menuPrice;

        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        for(JsonElement element : array)
        {
            menuName = element.getAsJsonObject().get(Define.KEY_MENU_NAME).toString().replace("\"", "");
            menuPrice = element.getAsJsonObject().get(Define.KEY_MENU_PRICE).toString();

            Log.i(LOG, "=========> menuName : " + menuName);
            Log.i(LOG, "=========> menuPrice : " + menuPrice);

            MenuInfo info = new MenuInfo(menuName, menuPrice);
            menuList.add(info);
        }

        if(mStoreManagerView != null)
            mStoreManagerView.setTextStoreMenu(menuList);
    }

    private void retryDialog(String comment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(comment)
                .setNegativeButton("다시 시도", null)
                .create()
                .show();
    }

    private void setServiceToView()
    {
        mStoreManagerView.setService(mService);
//        mRegistMenuView.setService(mService);
    }

    private void clearView()
    {
        mGuideLoginView.setVisible(false);
        mGuideOpenView.setVisible(false);
        mStoreManagerView.setVisible(false);
    }

    public final static int MSG_CLEAR_VIEW = 1000;
    public final static int MSG_ENTER_GUIDE_LOGIN_VIEW = MSG_CLEAR_VIEW + 1;
    public final static int MSG_ENTER_GUIDE_OPEN_VIEW = MSG_ENTER_GUIDE_LOGIN_VIEW + 1;
    public final static int MSG_ENTER_STORE_MANAGER_VIEW = MSG_ENTER_GUIDE_OPEN_VIEW + 1;
    public final static int MSG_SUCCESS_LOGIN = MSG_ENTER_STORE_MANAGER_VIEW + 1;

    private final static int DELAY_CHECK_SERVICE_STARTED = 500;

    private StoreManagerHandler mStoreManagerHandler = new StoreManagerHandler();

    private class StoreManagerHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_CLEAR_VIEW:
                    clearView();
                    break;

                case MSG_ENTER_GUIDE_LOGIN_VIEW:
                    clearView();
                    mGuideLoginView.setVisible(true);
                    break;

                case MSG_ENTER_GUIDE_OPEN_VIEW:
                    clearView();
                    mGuideOpenView.setVisible(true);
                    break;

                case MSG_ENTER_STORE_MANAGER_VIEW:
                    clearView();
                    mStoreManagerView.setVisible(true);
                    break;

                case MSG_SUCCESS_LOGIN:
                    clearView();
                    break;

                default:
                    break;
            }
        }
    }
}
