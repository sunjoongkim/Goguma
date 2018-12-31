package com.wowls.goguma.ui.store;

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
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wowls.goguma.R;
import com.wowls.goguma.data.MenuInfo;
import com.wowls.goguma.define.Define;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.service.GogumaService;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoreFragment extends Fragment
{
    private static final String LOG = "Goguma";

    private Context mContext;

    private LoginView mLoginView;
    private NotiPlaceView mNotiPlaceView;
    private RegistMenuView mRegistMenuView;

    private TextView mBtnNoti, mBtnRegist;

    private GogumaService mService;
    private RetrofitService mRetrofitService;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Log.i(LOG, "==========================> StoreFragment onCreateView");
        View view = inflater.inflate(R.layout.producer_main, container, false);

        initRetrofit();
        mContext = getContext();
        mService = GogumaService.getService();

        mLoginView = new LoginView(mContext, view.findViewById(R.id.login_view), mProducerHandler, mRetrofitService);
        mNotiPlaceView = new NotiPlaceView(mContext, getActivity(), view.findViewById(R.id.notify_view), mRetrofitService);
        mRegistMenuView = new RegistMenuView(mContext, view.findViewById(R.id.regist_menu_view), mRetrofitService);

        mBtnNoti = (TextView) view.findViewById(R.id.btn_notify);
        mBtnNoti.setOnClickListener(mOnClickListener);
        mBtnRegist = (TextView) view.findViewById(R.id.btn_regist);
        mBtnRegist.setOnClickListener(mOnClickListener);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        setServiceToView();
        checkLogin();
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
        if(mService != null && mService.getCurrentUser().isEmpty())
            mLoginView.setVisible(true);
        else
            getMenuList();
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

    private void menuParser(String json)
    {
        Log.i(LOG, "=============> showOwnMenuList : " + json);

        ArrayList<MenuInfo> menuList = new ArrayList<>();

        String storeId;
        String menuName;
        String menuPrice;

        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        for(JsonElement element : array)
        {
            storeId = element.getAsJsonObject().get("store_id").toString().replace("\"", "");
            menuName = element.getAsJsonObject().get("menu_name").toString().replace("\"", "");
            menuPrice = element.getAsJsonObject().get("menu_price").toString();

            Log.i(LOG, "=========> storeId : " + storeId);
            Log.i(LOG, "=========> menuName : " + menuName);
            Log.i(LOG, "=========> menuPrice : " + menuPrice);

            MenuInfo info = new MenuInfo(storeId, menuName, menuPrice);
            menuList.add(info);
        }

        if(mRegistMenuView != null)
            mRegistMenuView.setMenuList(menuList);
    }

    private void retryDialog(String comment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
                case R.id.btn_notify:
                    mNotiPlaceView.setVisible(true);
                    break;

                case R.id.btn_regist:
                    mRegistMenuView.setVisible(true);
                    break;
            }
        }
    };

    private void setServiceToView()
    {
        mLoginView.setService(mService);
        mNotiPlaceView.setService(mService);
        mRegistMenuView.setService(mService);
    }

    private void clearView()
    {
        mLoginView.setVisible(false);
        mNotiPlaceView.setVisible(false);
        mRegistMenuView.setVisible(false);
    }

    public final static int MSG_CLEAR_VIEW = 1000;
    public final static int MSG_ENTER_LOGIN_VIEW = MSG_CLEAR_VIEW + 1;
    public final static int MSG_ENTER_REGIST_MENU_VIEW = MSG_ENTER_LOGIN_VIEW + 1;
    public final static int MSG_ENTER_NOTI_VIEW = MSG_ENTER_REGIST_MENU_VIEW + 1;
    public final static int MSG_SUCCESS_LOGIN = MSG_ENTER_NOTI_VIEW + 1;

    private final static int DELAY_CHECK_SERVICE_STARTED = 500;

    private ProducerHandler mProducerHandler = new ProducerHandler();

    private class ProducerHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_CLEAR_VIEW:
                    clearView();
                    break;

                case MSG_ENTER_LOGIN_VIEW:
                    clearView();
                    mLoginView.setVisible(true);
                    break;

                case MSG_ENTER_REGIST_MENU_VIEW:
                    clearView();
                    mRegistMenuView.setVisible(true);
                    break;

                case MSG_ENTER_NOTI_VIEW:
                    clearView();
                    mNotiPlaceView.setVisible(true);
                    break;

                case MSG_SUCCESS_LOGIN:
                    clearView();
                    getMenuList();
                    break;

                default:
                    break;
            }
        }
    }
}
