package com.wowls.boddari.ui.search;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wowls.boddari.R;
import com.wowls.boddari.data.StoreInfo;
import com.wowls.boddari.define.Define;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.retrofit.ReverseGeoService;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.custom.RemoveScrollNMapView;
import com.wowls.boddari.ui.search.adapter.SearchListAdapter;
import com.wowls.boddari.ui.search.adapter.SearchPagerAdapter;

import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity
{
    public final static String LOG = "Goguma";

    private LocationManager mLocationManager;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int DEFAULT_ZOOM_LEVEL = 15;

    private NaverMap mNaverMap;

    private GogumaService mService;
    private RetrofitService mRetrofitService;
    private ReverseGeoService mReverseGeoService;

    private FusedLocationSource mLocationSource;
    private RemoveScrollNMapView mMapView;
    private LocationOverlay mLocationOverlay;
    private UiSettings mUiSettings;

    private Marker mPreSelectedMarker;

    private DrawerLayout mDrawerLayout;
    private SlidingUpPanelLayout mSlideLayout;
    private MenuView mMenuView;
    private SlideView mSlideView;

    private TextView mSearchTitle;
    private TextView mTextAddress;
    private EditText mEditKeyword;
    private ImageView mBtnMenu;
    private ImageView mBtnSearch;

    private SearchListAdapter mListAdapter;

    private ViewPager mSearchViewPager;
    private SearchPagerAdapter mPagerAdapter;

    private RecyclerView mListView;

    private ArrayList<StoreInfo> mStoreList = new ArrayList<>();
    private ArrayList<Marker> mMarkerList = new ArrayList<>();

    private boolean mIsInitMap = true;
    private boolean mIsLoadedStore = false;
    private boolean mIsListMode = false;

    private Location mCurrentPoint;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);

        Log.i(LOG, "=====================> CustomGalleryActivity onCreate ");
        mService = GogumaService.getService();

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkGpsState();

        initRetrofit();
        initGeoServiceRetrofit();
        mLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        mMapView = (RemoveScrollNMapView) findViewById(R.id.mapView);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_container);
        mSlideLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mMenuView = new MenuView(this, findViewById(R.id.view_menu), mMenuItemClickListener);
        mSlideView = new SlideView(this, findViewById(R.id.view_slide), mSlideViewListener);

        mSearchTitle = (TextView) findViewById(R.id.search_title);
        mTextAddress = (TextView) findViewById(R.id.text_address);
        mEditKeyword = (EditText) findViewById(R.id.edit_keyword);
        mBtnMenu = (ImageView) findViewById(R.id.btn_menu);
        mBtnMenu.setOnClickListener(mOnClickListener);

        mBtnSearch = (ImageView) findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(mOnClickListener);
        mSearchViewPager = (ViewPager) findViewById(R.id.search_view_pager);
        mListView = (RecyclerView) findViewById(R.id.search_list_view);
        mListView.setHasFixedSize(true);

        initSlideLayout();
        initMap();

    }

    @Override
    protected void onDestroy()
    {
        Log.i(LOG, "=====================> CustomGalleryActivity onDestroy ");
        mLocationSource = null;
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(mLocationSource.onRequestPermissionsResult(requestCode, permissions, grantResults))
            return;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // GPS on/off 체크
    private void checkGpsState()
    {
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void initSlideLayout()
    {
        mSlideLayout.getChildAt(1).setOnClickListener(null);
        mSlideLayout.setFadeOnClickListener(v -> {
            mSlideLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        });

        mSlideLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener()
        {
            @Override
            public void onPanelSlide(View panel, float slideOffset)
            {
                mSlideView.updateSlideLayout(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState)
            {
                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    mSearchViewPager.setVisibility(View.INVISIBLE);
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    mSearchViewPager.setVisibility(View.VISIBLE);
                    moveNearestStore();
                }
            }
        });
    }

    public void initMap()
    {
        mIsInitMap = true;

        mMapView.getMapAsync(mMapReadyCallback);

        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
    }

    private void initRetrofit()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Define.URL_BASE)
                .build();

        mRetrofitService = retrofit.create(RetrofitService.class);
    }

    private void initGeoServiceRetrofit()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Define.URL_NAVER_API)
                .build();

        mReverseGeoService = retrofit.create(ReverseGeoService.class);
    }

    private void initPagerView()
    {
        Log.i(LOG, "===========================> initPagerView : " + mCurrentPoint);
        Log.e(LOG, "===========================> initPagerView mStoreList : " + mStoreList);
        mPagerAdapter = new SearchPagerAdapter(this, getSupportFragmentManager(), mStoreList);
        mSearchViewPager.setAdapter(mPagerAdapter);
        mSearchViewPager.setOnPageChangeListener(mPagerAdapter);
        mPagerAdapter.notifyDataSetChanged();

        mSearchViewPager.setCurrentItem(0);
        mSearchViewPager.setOffscreenPageLimit(3);
    }

    private void unregistAdapter()
    {
        mSearchViewPager.setAdapter(null);
    }

    public ViewPager getViewPager()
    {
        return mSearchViewPager;
    }

    public void onPageSelected(int index)
    {
        Log.e(LOG, "onPageSelected index : " +  index);

        LatLng point = mMarkerList.get(index).getPosition();
        mNaverMap.moveCamera(CameraUpdate.toCameraPosition(new CameraPosition(point, DEFAULT_ZOOM_LEVEL)).animate(CameraAnimation.Fly, 500));
        setSelectedMarker(mMarkerList.get(index), true);

        mPreSelectedMarker = mMarkerList.get(index);
    }

    private void initListAdapter()
    {
        Log.e(LOG, "====================> initListAdapter");

        mListAdapter = new SearchListAdapter(mStoreList, this, mRetrofitService);
        mListView.setAdapter(mListAdapter);
    }

    private OnMapReadyCallback mMapReadyCallback = new OnMapReadyCallback()
    {
        @Override
        public void onMapReady(@NonNull NaverMap naverMap)
        {
            mNaverMap = naverMap;

            initMapSetting(naverMap);

            mNaverMap.setOnMapClickListener(mOnMapClickListener);
            mNaverMap.setLocationSource(mLocationSource);
            mNaverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
            mNaverMap.addOnLocationChangeListener(mOnLocationChangeListener);

            getStores(null);
        }
    };

    private NaverMap.OnLocationChangeListener mOnLocationChangeListener = new NaverMap.OnLocationChangeListener()
    {
        @Override
        public void onLocationChange(@NonNull Location location)
        {
            mCurrentPoint = location;
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

            if(mService != null)
                mService.setCurrentLocation(location.getLatitude(), location.getLongitude());

            getCurrentAddress(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

            if(mIsInitMap)
            {
                mNaverMap.moveCamera(CameraUpdate.toCameraPosition(new CameraPosition(position, DEFAULT_ZOOM_LEVEL)).animate(CameraAnimation.Fly, 500));
                mIsInitMap = false;

                if(mIsLoadedStore)
                {
                    sortStoreList();
                    initPagerView();
                    initListAdapter();
                }
            }
        }
    };

    //    private NMapActivity.OnDataProviderListener mOnDataProviderListener = new NMapActivity.OnDataProviderListener()
    //    {
    //        @Override
    //        public void onReverseGeocoderResponse(NMapPlacemark nMapPlacemark, NMapError nMapError)
    //        {
    //            if(nMapError == null && nMapPlacemark != null)
    //            {
    //                Intent broadcast = new Intent();0
    //                broadcast.setAction("action_store_address");
    //                broadcast.putExtra("store_address", nMapPlacemark.toString());
    //                mContext.sendBroadcast(broadcast);
    //            }
    //        }
    //    };

    private void initMapSetting(@NonNull NaverMap naverMap)
    {
        mUiSettings = naverMap.getUiSettings();
        mUiSettings.setLogoClickEnabled(false);
        mUiSettings.setLocationButtonEnabled(true);
        mUiSettings.setCompassEnabled(true);
    }

    private void getStores(final String[] keywords)
    {
        mIsLoadedStore = false;

        Call<ResponseBody> callStoreList;

        if(mRetrofitService != null)
        {
            if(keywords == null || keywords.length == 0)
                callStoreList = mRetrofitService.showStoreList();
            else
                callStoreList = mRetrofitService.showStoreList(keywords);

            callStoreList.enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response)
                {
                    if(response.body() == null)
                    {
                        retryDialog("점포 가져오기 실패");
                        return;
                    }

                    try {
                        String json = response.body().string();
                        Log.i(LOG, "==================================> getStores : " + json);

                        storeListParser(json);
                        initEditText();

                        if(keywords != null && keywords.length != 0)
                        {
                            if(mStoreList.isEmpty())
                                retryDialog("검색된 점포가 없습니다.");
                            else {
                                moveNearestStore();
                                mSlideLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                            }
                        }
                    }
                    catch (IOException e) {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Log.i(LOG, "onFailure : " + t.toString());
                }
            });
        }
    }

    private void retryDialog(String comment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(comment)
                .setNegativeButton("다시 시도", null)
                .create()
                .show();
    }

    private void storeListParser(String json)
    {
        if(!mStoreList.isEmpty())
            mStoreList.clear();

        String storeName;
        String longitude;
        String latitude;
        String storeId;

        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        for(JsonElement element : array)
        {
            if(element.isJsonNull())
                return;

            JsonObject object = element.getAsJsonObject();

            longitude = object.get(Define.KEY_STORE_LON).toString();
            latitude = object.get(Define.KEY_STORE_LAT).toString();
            storeId = object.get(Define.KEY_STORE_ID).toString().replace("\"", "");

            if(object.get(Define.KEY_STORE_NAME) == null)
                storeName = storeId;
            else
                storeName = object.get(Define.KEY_STORE_NAME).toString().replace("\"", "");

            Log.i(LOG, "=========> longitude : " + longitude);
            Log.i(LOG, "=========> latitude : " + latitude);
            Log.i(LOG, "=========> storeId : " + storeId);
            Log.i(LOG, "=========> storeName : " + storeName);

            StoreInfo info = new StoreInfo(storeName, Double.parseDouble(longitude), Double.parseDouble(latitude), storeId);
            mStoreList.add(info);
        }

        mIsLoadedStore = true;

        if(!mIsInitMap)
        {
            sortStoreList();
            initPagerView();
            initListAdapter();
        }
    }

    private void sortStoreList()
    {
        if(mCurrentPoint == null || mStoreList == null)
        {
            mSearchHandler.sendEmptyMessageDelayed(MSG_SORT_STORE_LIST, 500);
            return;
        }

        for(StoreInfo info : mStoreList)
            info.setDistance(mCurrentPoint);

        Collections.sort(mStoreList, new Comparator<StoreInfo>()
        {
            @Override
            public int compare(StoreInfo store1, StoreInfo store2)
            {
                if(store1.getDistance() > store2.getDistance())
                    return 1;
                else
                    return -1;
            }
        });

        addStoreMarker();
    }

    public void getStoreAddress(double lon, double lat)
    {
        //        if(mMapContext != null)
        //            mMapContext.findPlacemarkAtLocation(lon, lat);
    }

    private void addStoreMarker()
    {
        if(!mMarkerList.isEmpty()) {
            for (Marker marker : mMarkerList) {
                marker.setMap(null);
            }
            mMarkerList.clear();
        }

        for (StoreInfo info : mStoreList)
        {
            Log.e("LOG", "========> info : " + info.getStoreName());
            double longitude = info.getLongitude();
            double latitude = info.getLatitude();

            final Marker marker = new Marker();
            marker.setPosition(new LatLng(latitude, longitude));

            marker.setTag(info.getStoreName());
            marker.setOnClickListener(mOnMarkerClickListener);
            setSelectedMarker(marker, false);

            marker.setMap(mNaverMap);
            mMarkerList.add(marker);
        }
    }

    private void setSelectedMarker(Marker marker, boolean isSelected)
    {
        if(marker == null)
            return;

        if(isSelected)
        {
            marker.setIcon(MarkerIcons.RED);
            marker.setZIndex(1);
            marker.setForceShowIcon(true);

            setSelectedMarker(mPreSelectedMarker, false);
        }
        else
        {
            marker.setIcon(Marker.DEFAULT_ICON);
            marker.setZIndex(0);
            marker.setForceShowIcon(false);
        }
    }

    private NaverMap.OnMapClickListener mOnMapClickListener = new NaverMap.OnMapClickListener()
    {
        @Override
        public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng)
        {
            setSelectedMarker(mPreSelectedMarker, false);
            mSearchViewPager.setVisibility(View.INVISIBLE);
        }
    };

    private Overlay.OnClickListener mOnMarkerClickListener = new Overlay.OnClickListener()
    {
        @Override
        public boolean onClick(@NonNull Overlay overlay)
        {
            Marker marker = (Marker) overlay;

            if(Marker.DEFAULT_ICON.equals(marker.getIcon()))
            {
                Log.e(LOG, "=======> onClick marker : " + marker.getTag());
                Log.e(LOG, "=======> onClick index : " + mMarkerList.indexOf(marker));
                setSelectedMarker(marker, true);
                mPreSelectedMarker = marker;

                mSearchViewPager.setCurrentItem(mMarkerList.indexOf(marker));
                mSearchViewPager.setVisibility(View.VISIBLE);

                mNaverMap.moveCamera(CameraUpdate.toCameraPosition(new CameraPosition(marker.getPosition(), DEFAULT_ZOOM_LEVEL)).animate(CameraAnimation.Fly, 500));
            }
            else
            {
                setSelectedMarker(marker, false);
                mSearchViewPager.setVisibility(View.INVISIBLE);
            }

            return true;
        }
    };

    private void initEditText()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditKeyword.getWindowToken(), 0);

        mEditKeyword.setText("");
    }

    private void moveNearestStore()
    {
        mMarkerList.get(0).setIcon(MarkerIcons.RED);
        LatLng position = mMarkerList.get(0).getPosition();

        mNaverMap.moveCamera(CameraUpdate.toCameraPosition(new CameraPosition(position, DEFAULT_ZOOM_LEVEL)).animate(CameraAnimation.Fly, 500));

        mSearchViewPager.setCurrentItem(0);
//        mSearchViewPager.setVisibility(View.VISIBLE);
    }

    private MenuView.MenuItemClickListener mMenuItemClickListener = new MenuView.MenuItemClickListener()
    {
        @Override
        public void onClickItem(Menu item)
        {

        }
    };

    private SlideView.SlideViewListener mSlideViewListener = new SlideView.SlideViewListener()
    {
        @Override
        public void onCloseSlide()
        {

        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_menu:
                    View menu = findViewById(R.id.view_menu);
                    mDrawerLayout.openDrawer(menu);
                    break;

                case R.id.btn_search:
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditKeyword.getWindowToken(), 0);

                    if(mSlideView.getTitle().equals(""))
                        mSearchTitle.setText("모든 상점");
                    else
                        mSearchTitle.setText(mSlideView.getTitle());

                    mSearchHandler.sendEmptyMessageDelayed(MSG_GET_STORES, DELAY_GET_STORES);
                    break;
            }
        }
    };

    private String[] keywordParser(String keywords)
    {
        if(keywords.contains(" "))
            return keywords.split(" ");
        else
        {
            String[] string = new String[1];
            string[0] = keywords;

            return string;
        }
    }

    private void getCurrentAddress(String lat, String lon)
    {
        String coord = lon + "," + lat;

        if(mReverseGeoService != null)
        {
            mReverseGeoService.getAddress(coord, "json").enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    if(response.body() == null)
                        return;
                    else
                    {
                        try {
                            String json = response.body().string();
                            addressParser(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {

                }
            });
        }
    }

    private void addressParser(String json)
    {
        JsonParser parser = new JsonParser();

        String results;
        JsonElement element = (JsonElement) parser.parse(json);
        JsonObject object = element.getAsJsonObject();
        results = object.get("results").toString();

        String region;
        JsonArray array = (JsonArray) parser.parse(results);

        for(JsonElement element1 : array)
        {
            JsonObject object1 = element1.getAsJsonObject();
            region = object1.get("region").toString();

            String area1;
            String area2;
            String area3;
            JsonElement element2 = (JsonElement) parser.parse(region);
            JsonObject object2 = element2.getAsJsonObject();
            area1 = object2.get("area1").toString();
            area2 = object2.get("area2").toString();
            area3 = object2.get("area3").toString();

            mTextAddress.setText("");

            areaParser(area1);
            areaParser(area2);
            areaParser(area3);
        }
    }

    private void areaParser(String json)
    {
        JsonParser parser = new JsonParser();
        JsonElement element = (JsonElement) parser.parse(json);

        String area;

        JsonObject object = element.getAsJsonObject();

        area = object.get("name").toString().replace("\"", "");

        mTextAddress.append(area + " ");
    }

    private class GetStoreListTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids)
        {

            return null;
        }
    }

    public final static int MSG_SET_TRACKING_MODE = 1000;
    public final static int MSG_GET_LOCATION_DOT = MSG_SET_TRACKING_MODE + 1;
    public final static int MSG_SORT_STORE_LIST = MSG_GET_LOCATION_DOT + 1;
    public final static int MSG_GET_STORES = MSG_SORT_STORE_LIST + 1;

    private final static int DELAY_SET_TRACKING_MODE = 5000;
    private final static int DELAY_GET_STORES = 300;

    private SearchHandler mSearchHandler = new SearchHandler();

    private class SearchHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {

                case MSG_SET_TRACKING_MODE:
                    MapView mapView = (MapView) msg.obj;
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                    break;

                case MSG_GET_LOCATION_DOT:
                    break;

                case MSG_SORT_STORE_LIST:
                    sortStoreList();
                    break;

                case MSG_GET_STORES:
                    getStores(keywordParser(mEditKeyword.getText().toString()));
                    break;

                default:
                    break;
            }
        }
    }
}
