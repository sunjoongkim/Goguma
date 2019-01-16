package com.wowls.bottari.data;

import com.nhn.android.maps.maplib.NGeoPoint;

public class StoreInfo
{
    private String mStoreName;
    private double mLongitude;
    private double mLatitude;
    private String mStoreId;
    private double mStoreDistance;
    private String mStoreAddress;

    public StoreInfo(String name, double lon, double lat, String storeId)
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

    public double getLongitude()
    {
        return mLongitude;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public String getStoreId()
    {
        return mStoreId;
    }

    public void setDistance(NGeoPoint srcPoint)
    {
        mStoreDistance = NGeoPoint.getDistance(srcPoint, new NGeoPoint(mLongitude, mLatitude));
    }

    public double getDistance()
    {
        return mStoreDistance;
    }

    public void setStoreAddress(String address)
    {
        mStoreAddress = address;
    }

    public String getStoreAddress()
    {
        return mStoreAddress;
    }

}
