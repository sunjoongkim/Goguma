package com.wowls.goguma.define;


public class Define
{
    // URL
    public static final String URL_BASE = "http://211.39.150.247:7701";
//    public static final String URL_BASE = "https://webapp-wowls-wordchain-dev.azurewebsites.net/";
    public static final String URL_USER = "/users";
    public static final String URL_ROOM = "/rooms";

    public static final String URL_STORE_MANAGER = "/store-management";
    public static final String URL_STORE = "/stores";
    public static final String URL_OWNER = "/owners";

    public static final String URL_SEARCH = "/search";
    public static final String URL_KEYWORDS = "keywords";

    // Web socket
//    public static final String WS_BASE = "ws://172.30.1.58:7701/ws/websocket";
    public static final String WS_BASE = "ws://webapp-wowls-wordchain-dev.azurewebsites.net/ws/websocket";

    // key
    public static final String KEY_STORE_NAME = "store_name";
    public static final String KEY_STORE_LAT = "store_lat";
    public static final String KEY_STORE_LON = "store_lon";
    public static final String KEY_STORE_DESC = "store_desc";
    public static final String KEY_STORE_ID = "store_id";
    public static final String KEY_OWNER_ID = "owner_id";

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_PW = "user_pw";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_ENABLED = "enabled";

    public static final String KEY_MENU_STORE_ID = "storeId";
    public static final String KEY_MENU_NAME = "menuName";
    public static final String KEY_MENU_PRICE = "menuPrice";

    // msg
    public static final String MSG_MATCH = "match";
    public static final String MSG_EXIT = "exit";
    public static final String MSG_WORD = "word";
    public static final String MSG_INFO = "info";

    // type
    public static final String TYPE_ADMIN = "ADM";
    public static final String TYPE_PRODUCER = "PRO";
    public static final String TYPE_CONSUMER = "CON";
}
