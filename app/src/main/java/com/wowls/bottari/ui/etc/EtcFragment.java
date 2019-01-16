package com.wowls.bottari.ui.etc;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
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

import com.wowls.bottari.R;
import com.wowls.bottari.service.GogumaService;
import com.wowls.bottari.ui.etc.etc.EtcView;
import com.wowls.bottari.ui.etc.setting.EtcSettingView;
import com.wowls.bottari.ui.etc.user.EtcUserLoginView;
import com.wowls.bottari.ui.etc.user.EtcUserView;

public class EtcFragment extends Fragment
{
    private static final String LOG = "Goguma";

    private static final int TAB_USER = 0;
    private static final int TAB_SETTING = 1;
    private static final int TAB_ETC = 2;

    private static EtcFragment mMyFragment;

    private Context mContext;
    private GogumaService mService;

    private TextView mBtnUser, mBtnSetting, mBtnEtc;

    private EtcView mEtcView;
    private EtcSettingView mEtcSettingView;
    private EtcUserView mEtcUserView;
    private EtcUserLoginView mLoginView;

    public static EtcFragment getInstance()
    {
        Bundle args = new Bundle();

        EtcFragment fragment = new EtcFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static EtcFragment getFragment()
    {
        return mMyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.etc_main, container, false);

        mContext = getContext();
        mService = GogumaService.getService();

        mBtnUser = (TextView) view.findViewById(R.id.tab_user);
        mBtnUser.setOnClickListener(mOnClickListener);
        mBtnUser.setBackgroundColor(Color.parseColor("#A0FFF3"));

        mBtnSetting = (TextView) view.findViewById(R.id.tab_setting);
        mBtnSetting.setOnClickListener(mOnClickListener);
        mBtnEtc = (TextView) view.findViewById(R.id.tab_etc);
        mBtnEtc.setOnClickListener(mOnClickListener);

        mEtcUserView = new EtcUserView(mContext, view.findViewById(R.id.etc_user_view), mUserHandler);
        mEtcUserView.setVisible(true);
        mLoginView = new EtcUserLoginView(mContext, view.findViewById(R.id.user_login_view), mUserHandler);

        mEtcSettingView = new EtcSettingView(mContext, view.findViewById(R.id.etc_setting_view));
        mEtcView = new EtcView(mContext, view.findViewById(R.id.etc_3_view));

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        checkLogin();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    private void retryDialog(String comment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(comment)
                .setNegativeButton("다시 시도", null)
                .create()
                .show();
    }

    private void clearView()
    {
        mBtnUser.setBackgroundColor(Color.parseColor("#999999"));
        mBtnSetting.setBackgroundColor(Color.parseColor("#999999"));
        mBtnEtc.setBackgroundColor(Color.parseColor("#999999"));

        mEtcUserView.setVisible(false);
        mEtcSettingView.setVisible(false);
        mEtcView.setVisible(false);
        mLoginView.setVisible(false);
    }

    public void checkLogin()
    {
        if(mService != null)
        {
            if(mService.getCurrentUser().isEmpty())
                mLoginView.setVisible(true);
            else
                mEtcUserView.setVisible(true);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.tab_user:
                    clearView();
                    mBtnUser.setBackgroundColor(Color.parseColor("#A0FFF3"));

                    checkLogin();
                    break;

                case R.id.tab_setting:
                    clearView();
                    mBtnSetting.setBackgroundColor(Color.parseColor("#A0FFF3"));
                    mEtcSettingView.setVisible(true);
                    break;

                case R.id.tab_etc:
                    clearView();
                    mBtnEtc.setBackgroundColor(Color.parseColor("#A0FFF3"));
                    mEtcView.setVisible(true);
                    break;
            }
        }
    };



    public final static int MSG_CLEAR_VIEW = 1000;
    public final static int MSG_ENTER_LOGIN_VIEW = MSG_CLEAR_VIEW + 1;
    public final static int MSG_ENTER_REGIST_MENU_VIEW = MSG_ENTER_LOGIN_VIEW + 1;
    public final static int MSG_ENTER_NOTI_VIEW = MSG_ENTER_REGIST_MENU_VIEW + 1;
    public final static int MSG_SUCCESS_LOGIN = MSG_ENTER_NOTI_VIEW + 1;
    public final static int MSG_SUCCESS_LOGOUT = MSG_SUCCESS_LOGIN + 1;

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
                    break;

                case MSG_ENTER_LOGIN_VIEW:
                    break;

                case MSG_ENTER_REGIST_MENU_VIEW:
                    break;

                case MSG_ENTER_NOTI_VIEW:
                    break;

                case MSG_SUCCESS_LOGIN:
                    checkLogin();
                    break;

                case MSG_SUCCESS_LOGOUT:
                    checkLogin();
                    break;

                default:
                    break;
            }
        }
    }
}
