package com.wowls.goguma.data;

public class MenuInfo
{
    private String mStoreId = "";
    private String mMenuName = "";
    private String mMenuPrice = "";

    public MenuInfo(String storeId, String menuName, String price)
    {
        mStoreId = storeId;
        mMenuName = menuName;
        mMenuPrice = price;
    }

    public void setStoreId(String id)
    {
        mStoreId = id;
    }

    public String getStoreId()
    {
        return mStoreId;
    }

    public void setMenuName(String name)
    {
        mMenuName = name;
    }

    public String getMenuName()
    {
        return mMenuName;
    }

    public void setMenuPrice(String price)
    {
        mMenuPrice = price;
    }

    public String getMenuPrice()
    {
        return mMenuPrice;
    }
}
