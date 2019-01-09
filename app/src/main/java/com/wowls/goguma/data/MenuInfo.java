package com.wowls.goguma.data;

public class MenuInfo
{
    private String mMenuName = "";
    private String mMenuPrice = "";

    public MenuInfo(String menuName, String price)
    {
        mMenuName = menuName;
        mMenuPrice = price;
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
