package com.wowls.boddari.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wowls.boddari.data.MenuInfo;
import com.wowls.boddari.define.ConnectionState;
import com.wowls.boddari.define.ViewState;

import java.util.ArrayList;

public class GogumaService extends Service
{
    public final static String LOG = "Goguma";

    private static GogumaService mService;

    private ConnectionState mConnectionState = ConnectionState.LOGOFF;
    private String mCurrentUser = "";
    private String mCurrentUserNick = "";
    private String mCurrentUserImage = "";

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
        Log.e(LOG, "=================> onCreate GogumaService");
        mService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(LOG, "=================> onStartCommand GogumaService");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        Log.e(LOG, "=================> onDestroy GogumaService");
        mService = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void setConnectionState(ConnectionState state, String user, String nick, String url)
    {
        Log.i(LOG, "===============> setConnectionState state : " + state);
        Log.i(LOG, "===============> setConnectionState user : " + user);
        Log.i(LOG, "===============> setConnectionState nick : " + nick);
        Log.i(LOG, "===============> setConnectionState url : " + url);

        mConnectionState = state;
        mCurrentUser = user;
        mCurrentUserNick = nick;
        mCurrentUserImage = url;
    }

    public String getCurrentUser()
    {
        return mCurrentUser;
    }

    public String getCurrentUserNick()
    {
        return mCurrentUserNick;
    }

    public String getCurrentUserImage()
    {
        return mCurrentUserImage;
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
        Log.i(LOG, "========> setExistStore exist : " + exist);

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


    public void setLoginListener(LoginListener listener)
    {
        mLoginListener = listener;
    }

    private LoginListener mLoginListener;

    public interface LoginListener
    {
        void onSuccessLogin();
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
                    setConnectionState(ConnectionState.LOGOFF, "", "", "");
                    break;

            }
        }
    }
}
