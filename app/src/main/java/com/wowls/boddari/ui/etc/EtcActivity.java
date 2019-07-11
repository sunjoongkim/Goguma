package com.wowls.boddari.ui.etc;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.wowls.boddari.R;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.etc.etc.EtcView;
import com.wowls.boddari.ui.etc.setting.EtcSettingView;
import com.wowls.boddari.ui.etc.user.EtcUserView;
import com.wowls.boddari.ui.store.GuideLoginView;

public class EtcActivity extends AppCompatActivity
{
    private static final String LOG = "Goguma";

    private GogumaService mService;

    private TextView mBtnUser, mBtnSetting, mBtnEtc;

    private EtcView mEtcView;
    private EtcSettingView mEtcSettingView;
    private EtcUserView mEtcUserView;
    private GuideLoginView mGuideLoginView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etc_main);

        mService = GogumaService.getService();

        mBtnUser = (TextView) findViewById(R.id.tab_user);
        mBtnUser.setOnClickListener(mOnClickListener);
        mBtnUser.setBackgroundColor(Color.parseColor("#A0FFF3"));

        mBtnSetting = (TextView) findViewById(R.id.tab_setting);
        mBtnSetting.setOnClickListener(mOnClickListener);
        mBtnEtc = (TextView) findViewById(R.id.tab_etc);
        mBtnEtc.setOnClickListener(mOnClickListener);

        mEtcUserView = new EtcUserView(this, this, findViewById(R.id.etc_user_view), mUserHandler);
        mEtcUserView.setVisible(true);
        mGuideLoginView = new GuideLoginView(this, findViewById(R.id.etc_guide_login_view));

        mEtcSettingView = new EtcSettingView(this, findViewById(R.id.etc_setting_view));
        mEtcView = new EtcView(this, findViewById(R.id.etc_3_view));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        mGuideLoginView.setVisible(false);
    }

    public void checkLogin()
    {
        if(mService != null)
        {
            mGuideLoginView.setVisible(false);

            if(mService.getCurrentUser().isEmpty())
                mGuideLoginView.setVisible(true);
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


    private GogumaService.LoginListener mLoginListener = new GogumaService.LoginListener()
    {
        @Override
        public void onSuccessLogin()
        {
//            checkLogin();
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
