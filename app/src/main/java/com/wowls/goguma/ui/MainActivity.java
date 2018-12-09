package com.wowls.goguma.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wowls.goguma.R;
import com.wowls.goguma.service.GogumaService;
import com.wowls.goguma.ui.consumer.ConsumerActivity;
import com.wowls.goguma.ui.producer.ProducerActivity;

public class MainActivity extends Activity
{
    public final static String LOG = "Goguma";

    private GogumaService mService;
    private Button mBtnCon, mBtnProd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mBtnCon = (Button) findViewById(R.id.btn_consumer);
        mBtnProd = (Button) findViewById(R.id.btn_producer);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        checkServiceStarted();
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
        {
            mService.setUiListener(mUiListener);
            mBtnCon.setOnClickListener(mOnClickListener);
            mBtnProd.setOnClickListener(mOnClickListener);
        }
    }

    private GogumaService.GogumaServiceListener mUiListener = new GogumaService.GogumaServiceListener()
    {
        @Override
        public void onFinish()
        {

        }
    };

    private void startConsumerActivity()
    {
        Intent intent = new Intent(this, ConsumerActivity.class);
        startActivity(intent);
    }

    private void startProducerActivity()
    {
        Intent intent = new Intent(this, ProducerActivity.class);
        startActivity(intent);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_consumer:
                    Log.i(LOG, "================> startConsumerActivity");
                    startConsumerActivity();
                    break;

                case R.id.btn_producer:
                    Log.i(LOG, "================> startProducerActivity");
                    startProducerActivity();
                    break;
            }
        }
    };


    public final static int MSG_CHECK_SERVICE_STARTED = 1000;

    private final static int DELAY_CHECK_SERVICE_STARTED = 500;

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

                default:
                    break;
            }
        }
    }
}
