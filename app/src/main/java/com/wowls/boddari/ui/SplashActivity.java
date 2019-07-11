package com.wowls.boddari.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.wowls.boddari.R;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.search.SearchActivity;


public class SplashActivity extends Activity
{
    public final static String LOG = "Goguma";

    private GogumaService mService;

    private boolean mHasNoPermission = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);

        mGogumaHandler.sendEmptyMessageDelayed(MSG_LOADING_COMPLETED, DELAY_LOADING_COMPLETED);
    }

    @Override
    protected void onStart()
    {
        Log.i(LOG, "===========> onStart : " + mService);
        super.onStart();

        checkServiceStarted();
        requestPermission();
    }

    @Override
    protected void onStop()
    {
        Log.i(LOG, "===========> onStop : " + mService);
        super.onStop();

        if(mService != null)
            mService.setUiListener(null);
    }

    private void checkServiceStarted()
    {
        mService = GogumaService.getService();
        Log.i(LOG, "===========> checkServiceStarted : " + mService);

        if(mService == null)
        {
            Intent i = new Intent(this, GogumaService.class);
            startService(i);

            mGogumaHandler.sendEmptyMessageDelayed(MSG_CHECK_SERVICE_STARTED, DELAY_CHECK_SERVICE_STARTED);
        }
        else
            mService.setUiListener(mUiListener);
    }

    // 위치관련 permission 체크
    private void requestPermission()
    {
        // 마쉬멜로 이상일 경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                mHasNoPermission = true;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else
                mHasNoPermission = false;
        }
    }

    private boolean checkPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            mHasNoPermission = false;
        else
            finish();
    }

    private GogumaService.GogumaServiceListener mUiListener = new GogumaService.GogumaServiceListener()
    {
        @Override
        public void onFinish()
        {

        }
    };

    private void startMainActivity()
    {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);

        finish();
    }

    public final static int MSG_CHECK_SERVICE_STARTED = 1000;
    public final static int MSG_LOADING_COMPLETED = MSG_CHECK_SERVICE_STARTED + 1;

    private final static int DELAY_CHECK_SERVICE_STARTED = 100;
    private final static int DELAY_LOADING_COMPLETED = 1500;
    private final static int DELAY_CHECK_PERMISSION = 300;

    private GogumaHandler mGogumaHandler = new GogumaHandler();

    private class GogumaHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {

                case MSG_CHECK_SERVICE_STARTED:
                    checkServiceStarted();
                    break;

                case MSG_LOADING_COMPLETED:
                    if(mHasNoPermission)
                        sendEmptyMessageDelayed(MSG_LOADING_COMPLETED, DELAY_CHECK_PERMISSION);
                    else
                        startMainActivity();
                    break;

                default:
                    break;
            }
        }
    }
}
