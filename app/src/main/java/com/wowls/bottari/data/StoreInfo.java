package com.wowls.bottari.data;

import com.nhn.android.maps.maplib.NGeoPoint;

public class StoreInfo
{
    private String mStoreName;
    private String mLongitude;
    private String mLatitude;
    private String mStoreId;
    private double mStoreDistance;

    public StoreInfo(String name, String lon, String lat, String storeId)
    {
        mStoreName = name;
        mLongitude = lon;
        mLatitude = lat;
        mStoreId = storeId;
    }

    public String getStoreName()
    {
        return mStoreName;
    }

    public String getLongitude()
    {
        return mLongitude;
    }

    public String getLatitude()
    {
        return mLatitude;
    }

    public String getStoreId()
    {
        return mStoreId;
    }

    public void setDistance(NGeoPoint srcPoint)
    {
        mStoreDistance = NGeoPoint.getDistance(srcPoint, new NGeoPoint(Double.parseDouble(mLongitude), Double.parseDouble(mLatitude)));
    }

    public double getDistance()
    {
        return mStoreDistance;
    }
}
