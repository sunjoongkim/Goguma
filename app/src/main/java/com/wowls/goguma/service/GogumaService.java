package com.wowls.goguma.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wowls.goguma.define.ConnectionState;
import com.wowls.goguma.define.ViewState;

public class GogumaService extends Service
{
    public final static String LOG = "Goguma";

    private static GogumaService mService;

    private ConnectionState mConnectionState = ConnectionState.LOGOFF;
    private String mCurrentUser = "";

//    private StompClient mClient;
    private ViewState mCurrentView;

    public static GogumaService getService()
    {
        return mService;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(LOG, "=================> onCreate GogumaService");
        mService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void setConnectionState(ConnectionState state, String user)
    {
        mConnectionState = state;
        mCurrentUser = user;
    }

    public String getCurrentUser()
    {
        return mCurrentUser;
    }

    public void setCurrentView(ViewState state)
    {
        mCurrentView = state;
    }

    public ViewState getCurrentView()
    {
        return mCurrentView;
    }

//    public void setClient(StompClient client)
//    {
//        mClient = client;
//    }
//
//    public StompClient getClient()
//    {
//        return mClient;
//    }

    public void setUiListener(GogumaServiceListener listener)
    {
        mUiListener = listener;

        if(mHandler.hasMessages(MSG_DISCONNECT_USER))
            mHandler.removeMessages(MSG_DISCONNECT_USER);

        if(listener == null)
            mHandler.sendEmptyMessageDelayed(MSG_DISCONNECT_USER, DELAY_DISCONNECT_USER);
    }


    private GogumaServiceListener mUiListener = null;

    public interface GogumaServiceListener
    {
        void onFinish();
    }


    private static final int MSG_DISCONNECT_USER = 100;

    private static final int DELAY_DISCONNECT_USER = 600000;

    private ServiceHandler mHandler = new ServiceHandler(Looper.getMainLooper());

    private class ServiceHandler extends Handler
    {
        public ServiceHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_DISCONNECT_USER:
                    setConnectionState(ConnectionState.LOGOFF, "");
                    break;

            }
        }
    }
}
