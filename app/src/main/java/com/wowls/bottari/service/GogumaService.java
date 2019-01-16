package com.wowls.bottari.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wowls.bottari.data.MenuInfo;
import com.wowls.bottari.define.ConnectionState;
import com.wowls.bottari.define.ViewState;

import java.util.ArrayList;

public class GogumaService extends Service
{
    public final static String LOG = "Goguma";

    private static GogumaService mService;

    private ConnectionState mConnectionState = ConnectionState.LOGOFF;
    private String mCurrentUser = "";
    private String mCurrentUserNick = "";

//    private StompClient mClient;
    private ViewState mCurrentView;
    private boolean mIsExistStore = false;

    private double mCurrentLatitude;
    private double mCurrentLongitude;

    private String mOpenStoreName;
    private double mOpenLatitude;
    private double mOpenLongitude;
    private String mOpenStoreDesc;
    private ArrayList<MenuInfo> mOpenMenuList = new ArrayList<>();

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

    public void setConnectionState(ConnectionState state, String user, String nick)
    {
        mConnectionState = state;
        mCurrentUser = user;
        mCurrentUserNick = nick;
    }

    public String getCurrentUser()
    {
        return mCurrentUser;
    }

    public String getCurrentUserNick()
    {
        return mCurrentUserNick;
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

    public void setCurrentLocation(double latitude, double longitude)
    {
        mCurrentLatitude = latitude;
        mCurrentLongitude = longitude;
    }

    public double getCurrentLatitude()
    {
        return mCurrentLatitude;
    }

    public double getCurrentLongitude()
    {
        return mCurrentLongitude;
    }

    public void setExistStore(boolean exist)
    {
        mIsExistStore = exist;
    }

    public boolean isExistStore()
    {
        return mIsExistStore;
    }

    // OpenStore get/set
    public void setOpenStoreName(String name)
    {
        mOpenStoreName = name;
    }

    public String getOpenStoreName()
    {
        return mOpenStoreName;
    }

    public void setOpenLatitude(double latitude)
    {
        mOpenLatitude = latitude;
    }

    public double getOpenLatitude()
    {
        return mOpenLatitude;
    }

    public void setOpenLongitude(double longitude)
    {
        mOpenLongitude = longitude;
    }

    public double getOpenLongitude()
    {
        return mOpenLongitude;
    }

    public void setOpenStoreDesc(String desc)
    {
        mOpenStoreDesc = desc;
    }

    public String getOpenStoreDesc()
    {
        return mOpenStoreDesc;
    }

    public void setOpenMenuList(ArrayList<MenuInfo> menuList)
    {
        mOpenMenuList = menuList;
    }

    public ArrayList<MenuInfo> getOpenMenuList()
    {
        return mOpenMenuList;
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
                    setConnectionState(ConnectionState.LOGOFF, "", "");
                    break;

            }
        }
    }
}
