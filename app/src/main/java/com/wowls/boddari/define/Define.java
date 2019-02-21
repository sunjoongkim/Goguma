package com.wowls.boddari.define;


public class Define
{
    // Client
    public static final String CLIENT_ID = "31yix7y141";
    public static final String CLIENT_SECRET = "ydrjkGSaBdFiNmDs689sWJoBd9EVup74QAd9ErXh";

    // Reverse Geocoding URL
    public static final String URL_NAVER_API = "https://naveropenapi.apigw.ntruss.com";
    public static final String URL_REVERSE_GEO = "/map-reversegeocode/v2/gc";

    public static final String URL_COORDS = "coords";
    public static final String URL_SOURCECRS = "sourcecrs";
    public static final String URL_ORDER = "orders";
    public static final String URL_OUTPUT = "output";
    public static final String HEADER_CLIENT_ID = "X-NCP-APIGW-API-KEY-ID";
    public static final String HEADER_CLIENT_SECRET = "X-NCP-APIGW-API-KEY";

    // Main server URL
    public static final String URL_BASE = "http://211.39.150.247:7701";
    public static final String URL_USER = "/users";
    public static final String URL_ROOM = "/rooms";

    public static final String URL_STORE_MANAGER = "/store-management";
    public static final String URL_IMAGE_STORAGE = "/image-storage";
    public static final String URL_STORE = "/stores";
    public static final String URL_OWNER = "/owners";
    public static final String URL_MENU = "/menus";
    public static final String URL_REVIEW = "/reviews";
    public static final String URL_WRITER = "/writers";
    public static final String URL_IMAGE = "/images";

    public static final String URL_SEARCH = "/search";
    public static final String URL_KEYWORDS = "keywords";
    public static final String URL_IMAGE_ORDERS = "imageOrder";

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
    public static final String KEY_STORE_ENABLED = "enabled";
    public static final String KEY_STORE_SCORE = "store_score";

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_PW = "user_pw";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_USER_NAME = "user_name";

    public static final String KEY_MENU_STORE_ID = "store_id";
    public static final String KEY_MENU_NAME = "menu_name";
    public static final String KEY_MENU_PRICE = "menu_price";
    public static final String KEY_MENU_ENABLED = "enabled";

    public static final String KEY_WRITER_ID = "writer_id";
    public static final String KEY_RATING = "rating";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_CRE_DATE = "cre_date";
    public static final String KEY_UPD_DATE = "upd_date";

    // msg
    public static final String MSG_MATCH = "match";
    public static final String MSG_EXIT = "exit";
    public static final String MSG_WORD = "word";
    public static final String MSG_INFO = "info";

    // type
    public static final String TYPE_ADMIN = "ADM";
    public static final String TYPE_PRODUCER = "PRO";
    public static final String TYPE_CONSUMER = "CON";

    // ViewPager
    public static final float BIG_SCALE = 1.0f;
    public static final float SMALL_SCALE = 0.7f;
    public static final float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    // Account by
    public static final String BY_NAVER = "n_";
    public static final String BY_KAKAO = "k_";
    public static final String BY_FACEBOOK = "f_";

    // picture
    public static final int PICTURE_REQUEST_CODE = 100;
}
