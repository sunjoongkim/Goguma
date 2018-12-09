package com.wowls.goguma.define;


public class Define
{
    // URL
    public static final String URL_BASE = "http://172.30.1.43:8080/";
//    public static final String URL_BASE = "https://webapp-wowls-wordchain-dev.azurewebsites.net/";
    public static final String URL_USER = "users/";
    public static final String URL_ROOM = "rooms/";

    public static final String URL_STORE_MANAGER = "store-management/";
    public static final String URL_STORE = "stores/";
    public static final String URL_OWNER = "owners/";

    public static final String URL_SEARCH = "search/";

    // Web socket
//    public static final String WS_BASE = "ws://172.30.1.58:7701/ws/websocket";
    public static final String WS_BASE = "ws://webapp-wowls-wordchain-dev.azurewebsites.net/ws/websocket";

    // key
    public static final String KEY_STORE_NAME = "STORE_NAME";
    public static final String KEY_STORE_LAT = "STORE_LAT";
    public static final String KEY_STORE_LON = "STORE_LON";
    public static final String KEY_STORE_DESC = "STORE_DESC";
    public static final String KEY_STORE_ID = "STORE_ID";
    public static final String KEY_OWNER_ID = "OWNER_ID";

    // msg
    public static final String MSG_MATCH = "match";
    public static final String MSG_EXIT = "exit";
    public static final String MSG_WORD = "word";
    public static final String MSG_INFO = "info";

    // type
    public static final String TYPE_ADMIN = "adm";
    public static final String TYPE_PRODUCER = "pro";
    public static final String TYPE_CONSUMER = "con";
}
