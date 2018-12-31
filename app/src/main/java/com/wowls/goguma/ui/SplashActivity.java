package com.wowls.goguma.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wowls.goguma.R;
import com.wowls.goguma.service.GogumaService;

public class SplashActivity extends Activity
{
    public final static String LOG = "Goguma";

    private GogumaService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);

        mWordChainHandler.sendEmptyMessageDelayed(MSG_LOADING_COMPLETED, DELAY_LOADING_COMPLETED);
    }

    @Override
    protected void onStart()
    {
        Log.i(LOG, "===========> onStart : " + mService);
        super.onStart();

        checkServiceStarted();
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

            mWordChainHandler.sendEmptyMessageDelayed(MSG_CHECK_SERVICE_STARTED, DELAY_CHECK_SERVICE_STARTED);
        }
        else
            mService.setUiListener(mUiListener);
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public final static int MSG_CHECK_SERVICE_STARTED = 1000;
    public final static int MSG_LOADING_COMPLETED = MSG_CHECK_SERVICE_STARTED + 1;

    private final static int DELAY_CHECK_SERVICE_STARTED = 100;
    private final static int DELAY_LOADING_COMPLETED = 1500;

    private WordChainHandler mWordChainHandler = new WordChainHandler();

    private class WordChainHandler extends Handler
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
                    startMainActivity();
                    finish();
                    break;

                default:
                    break;
            }
        }
    }
}
