package com.wowls.goguma.ui.etc;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wowls.goguma.R;
import com.wowls.goguma.define.Define;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.service.GogumaService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EtcFragment extends Fragment
{
    private static final String LOG = "Goguma";

    private Context mContext;

    private UserLoginView mLoginView;

    private TextView mBtnNoti, mBtnRegist;

    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private Handler mHandler;

    public static EtcFragment getInstance()
    {
        Bundle args = new Bundle();

        EtcFragment fragment = new EtcFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.etc_main, container, false);

        initRetrofit();
        mContext = getContext();
        mService = GogumaService.getService();

        mLoginView = new UserLoginView(mContext, view.findViewById(R.id.user_login_view), mUserHandler, mRetrofitService);

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
            }
        }
    };

    private void setServiceToView()
    {
        mLoginView.setService(mService);
    }

    private void clearView()
    {
        mLoginView.setVisible(false);
    }

    public final static int MSG_CLEAR_VIEW = 1000;
    public final static int MSG_ENTER_LOGIN_VIEW = MSG_CLEAR_VIEW + 1;
    public final static int MSG_ENTER_REGIST_MENU_VIEW = MSG_ENTER_LOGIN_VIEW + 1;
    public final static int MSG_ENTER_NOTI_VIEW = MSG_ENTER_REGIST_MENU_VIEW + 1;
    public final static int MSG_SUCCESS_LOGIN = MSG_ENTER_NOTI_VIEW + 1;

    private final static int DELAY_CHECK_SERVICE_STARTED = 500;

    private UserHandler mUserHandler = new UserHandler();

    private class UserHandler extends Handler
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
                    break;

                case MSG_ENTER_NOTI_VIEW:
                    clearView();
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
