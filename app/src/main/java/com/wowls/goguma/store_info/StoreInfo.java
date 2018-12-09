package com.wowls.goguma.store_info;

public class StoreInfo
{
    private String mStoreName;
    private String mLongitude;
    private String mLatitude;
    private String mStoreId;
    private String mOwnerId;

    public StoreInfo(String name, String lon, String lat, String storeId, String ownerId)
    {
        mStoreName = name;
        mLongitude = lon;
        mLatitude = lat;
        mStoreId = storeId;
        mOwnerId = ownerId;
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

    public String getOwnerId()
    {
        return mOwnerId;
    }

}
