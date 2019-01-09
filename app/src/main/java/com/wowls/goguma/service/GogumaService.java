package com.wowls.goguma.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wowls.goguma.data.MenuInfo;
import com.wowls.goguma.define.ConnectionState;
import com.wowls.goguma.define.ViewState;

import java.util.ArrayList;

public class GogumaService extends Service
{
    public final static String LOG = "Goguma";

    private static GogumaService mService;

    private ConnectionState mConnectionState = ConnectionState.LOGOFF;
    private String mCurrentUser = "";

//    private StompClient mClient;
    private ViewState mCurrentView;
    private boolean mIsExistStore = false;

    private String mStoreName;
    private double mLatitude;
    private double mLongitude;
    private String mStoreDesc;
    private ArrayList<MenuInfo> mMenuList = new ArrayList<>();

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

    public void setExistStore(boolean exist)
    {
        mIsExistStore = exist;
    }

    public boolean isExistStore()
    {
        return mIsExistStore;
    }

    // OpenStore get/set
    public void setStoreName(String name)
    {
        mStoreName = name;
    }

    public String getStoreName()
    {
        return mStoreName;
    }

    public void setLatitude(double latitude)
    {
        mLatitude = latitude;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public void setLongitude(double longitude)
    {
        mLongitude = longitude;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

    public void setStoreDesc(String desc)
    {
        mStoreDesc = desc;
    }

    public String getStoreDesc()
    {
        return mStoreDesc;
    }

    public void setMenuList(ArrayList<MenuInfo> menuList)
    {
        mMenuList = menuList;
    }

    public ArrayList<MenuInfo> getMenuList()
    {
        return mMenuList;
    }

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
