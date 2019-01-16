package com.wowls.bottari.ui.search;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.wowls.bottari.R;
import com.wowls.bottari.adapter.SearchListAdapter;
import com.wowls.bottari.adapter.SearchPagerAdapter;
import com.wowls.bottari.data.StoreInfo;
import com.wowls.bottari.define.Define;
import com.wowls.bottari.navermap.NMapPOIflagType;
import com.wowls.bottari.navermap.NMapViewerResourceProvider;
import com.wowls.bottari.retrofit.RetrofitService;
import com.wowls.bottari.service.GogumaService;
import com.wowls.bottari.ui.custom.RemoveScrollNMapView;

import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment
{
    private static final String LOG = "Goguma";

    private static final String CLIENT_ID = "31yix7y141";

    private NMapContext mMapContext;
    private NMapLocationManager mMapLocationManager;
    private NMapController mMapController;
    private NMapMyLocationOverlay mLocationOverlay;
    private NMapViewerResourceProvider mResourceProvider;
    private NMapOverlayManager mOverlayManager;
    private NMapPOIdataOverlay mPoiDataOverlay;
    private NMapPOIdata mPOIdata;

    private static SearchFragment mMyFragment;
    private GogumaService mService;

    private Context mContext;
    private LocationManager mLocationManager;
    private RemoveScrollNMapView mMapView;
    private View mMyView;

    private EditText mEditKeyword;
    private ImageView mBtnSearchMode;
    private ImageView mBtnSearch;

    private SearchListAdapter mListAdapter;
    private RetrofitService mRetrofitService;

    private ViewPager mSearchViewPager;
    private SearchPagerAdapter mPagerAdapter;

    private RecyclerView mListView;

    private ArrayList<StoreInfo> mStoreList = new ArrayList<>();

    private boolean mIsInitMap = true;
    private boolean mIsLoadedStore = false;
    private boolean mIsListMode = false;

    private NGeoPoint mCurrentPoint;


    public static SearchFragment getInstance()
    {
        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static SearchFragment getFragment()
    {
        return mMyFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(LOG, "==========================> SearchFragment onCreate");

        mMyFragment = this;
        mService = GogumaService.getService();
        mContext = getContext();

        mMapContext = new NMapContext(super.getActivity());
        mMapContext.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Log.i(LOG, "==========================> SearchFragment onCreateView");
        mMyView = inflater.inflate(R.layout.search_main, container, false);

        initRetrofit();

        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        checkGpsState();
        requestPermission();

        return mMyView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.i(LOG, "==========================> SearchFragment onActivityCreated");

        initMap();

        mEditKeyword = (EditText) mMyView.findViewById(R.id.edit_keyword);
        mBtnSearchMode = (ImageView) mMyView.findViewById(R.id.btn_search_mode);
        mBtnSearchMode.setOnClickListener(mOnClickListener);
        mBtnSearch = (ImageView) mMyView.findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(mOnClickListener);
        mListView = (RecyclerView) mMyView.findViewById(R.id.search_list_view);
        mListView.setHasFixedSize(true);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mMapContext.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mMapContext.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mMapContext.onPause();
    }

    @Override
    public void onStop()
    {
        mMapContext.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        mMyFragment = null;
        mMapContext.onDestroy();
        super.onDestroy();
    }

    public void initMap()
    {
        if(mMapView == null && mMyView != null)
        {
            mIsInitMap = true;

            mMapView = (RemoveScrollNMapView) mMyView.findViewById(R.id.mapView);
            mMapView.setNcpClientId(CLIENT_ID);
            mMapContext.setupMapView(mMapView);

            // initialize map view
            mMapView.setClickable(true);
            mMapView.setEnabled(true);
            mMapView.setFocusable(true);
            mMapView.setFocusableInTouchMode(true);
            mMapView.requestFocus();

            mMapView.setScalingFactor(3.0f, true);
            mMapController = mMapView.getMapController();

            mMapView.setOnMapStateChangeListener(mOnMapStateChangeListener);
            mMapView.setOnMapViewTouchEventListener(mOnMapTouchEventListener);

            mMapContext.setMapDataProviderListener(mOnDataProviderListener);

            mMapLocationManager = new NMapLocationManager(mContext);
            mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
            mMapLocationManager.enableMyLocation(true);

            mResourceProvider = new NMapViewerResourceProvider(mContext);
            mOverlayManager = new NMapOverlayManager(mContext, mMapView, mResourceProvider);
            mLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, null);
        }
    }

    private void initRetrofit()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Define.URL_BASE)
                .build();

        mRetrofitService = retrofit.create(RetrofitService.class);
    }

    private void initPagerView()
    {
        mSearchViewPager = (ViewPager) mMyView.findViewById(R.id.search_view_pager);

        Log.i(LOG, "===========================> initPagerView : " + mCurrentPoint);
        mPagerAdapter = new SearchPagerAdapter(this, getFragmentManager(), mStoreList);
        mSearchViewPager.setAdapter(mPagerAdapter);
        mSearchViewPager.setOnPageChangeListener(mPagerAdapter);
        mPagerAdapter.notifyDataSetChanged();

        mSearchViewPager.setCurrentItem(0);
        mSearchViewPager.setOffscreenPageLimit(3);
    }

    public ViewPager getViewPager()
    {
        return mSearchViewPager;
    }

    public void onPageSelected(int index)
    {
        Log.i(LOG, "onPageSelected index : " +  index);

        NGeoPoint point = mPOIdata.getPOIitem(index).getPoint();
        mMapController.animateTo(point, true);
        mPoiDataOverlay.selectPOIitem(mPOIdata.getPOIitem(index), true);
        Log.i(LOG, "===============> onPageSelected point : " + point);
    }

    private void initListAdapter()
    {
        mListAdapter = new SearchListAdapter(mStoreList, mContext, mRetrofitService);
        mListView.setAdapter(mListAdapter);
    }

    // GPS on/off 체크
    private void checkGpsState()
    {
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
        }
    }

    // 위치관련 permission 체크
    private void requestPermission()
    {
        // 마쉬멜로 이상일 경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkPermission())
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private boolean checkPermission()
    {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener()
    {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation)
        {
            Log.i(LOG, "==========================> onLocationChanged");

            mCurrentPoint = myLocation;

            if(mService != null)
                mService.setCurrentLocation(myLocation.getLatitude(), myLocation.getLongitude());

            if(mIsInitMap)
            {
                mMapController.setMapCenter(myLocation, 11);
                mIsInitMap = false;

                if(mIsLoadedStore)
                {
                    sortStoreList();
                    initPagerView();
                    initListAdapter();
                }
            }
            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager)
        {
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation)
        {
        }

    };

    private NMapView.OnMapStateChangeListener mOnMapStateChangeListener = new NMapView.OnMapStateChangeListener()
    {
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError)
        {
            Log.i(LOG, "==========================> onMapInitHandler");
            getStores(null);
        }

        @Override
        public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint)
        {
            Log.i(LOG, "==========================> onMapCenterChange");

        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView)
        {
            Log.i(LOG, "==========================> onMapCenterChangeFine");

        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int i)
        {
            Log.i(LOG, "==========================> onZoomLevelChange");

        }

        @Override
        public void onAnimationStateChange(NMapView nMapView, int i, int i1)
        {
            Log.i(LOG, "==========================> onAnimationStateChange");

        }
    };

    private NMapView.OnMapViewTouchEventListener mOnMapTouchEventListener = new NMapView.OnMapViewTouchEventListener()
    {

        @Override
        public void onLongPress(NMapView nMapView, MotionEvent motionEvent)
        {

        }

        @Override
        public void onLongPressCanceled(NMapView nMapView)
        {

        }

        @Override
        public void onTouchDown(NMapView nMapView, MotionEvent motionEvent)
        {

        }

        @Override
        public void onTouchUp(NMapView nMapView, MotionEvent motionEvent)
        {

        }

        @Override
        public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1)
        {

        }

        @Override
        public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent)
        {

        }
    };

    private NMapActivity.OnDataProviderListener mOnDataProviderListener = new NMapActivity.OnDataProviderListener()
    {
        @Override
        public void onReverseGeocoderResponse(NMapPlacemark nMapPlacemark, NMapError nMapError)
        {
            if(nMapError == null && nMapPlacemark != null)
            {
                Intent broadcast = new Intent();
                broadcast.setAction("action_store_address");
                broadcast.putExtra("store_address", nMapPlacemark.toString());
                mContext.sendBroadcast(broadcast);
            }
        }
    };

    private NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener()
    {
        @Override
        public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem)
        {
            Log.i(LOG, "=======================? onFocusChanged");

            if(mSearchViewPager != null)
            {
                if(nMapPOIitem != null && nMapPOIitem.isKeepSelected())
                {
                    mSearchViewPager.setCurrentItem(mPOIdata.indexOf(nMapPOIitem));
                    mSearchViewPager.setVisibility(View.VISIBLE);
                }
                else
                    mSearchViewPager.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public void onCalloutClick(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem)
        {
            Log.i(LOG, "=======================? onCalloutClick");

        }
    };

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
                            else
                                moveNearestStore();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
        if(mMapContext != null)
            mMapContext.findPlacemarkAtLocation(lon, lat);
    }

    private void addStoreMarker()
    {
        if(mOverlayManager != null)
        {
            mOverlayManager.clearOverlays();

            int markerId = NMapPOIflagType.PIN;
            int count = mStoreList.size();

            // set POI data
            mPOIdata = new NMapPOIdata(count, mResourceProvider);
            mPOIdata.beginPOIdata(count);

            for (StoreInfo info : mStoreList)
            {
                double longitude = info.getLongitude();
                double latitude = info.getLatitude();

                mPOIdata.addPOIitem(longitude, latitude, null, markerId, info.getStoreId());
            }

            mPOIdata.endPOIdata();

            mPoiDataOverlay = mOverlayManager.createPOIdataOverlay(mPOIdata, null);
            mPoiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
        }
    }

    private void initEditText()
    {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditKeyword.getWindowToken(), 0);

        mEditKeyword.setText("");
    }

    private void moveNearestStore()
    {
        mMapController.setZoomLevel(11);
        mMapController.animateTo(mPOIdata.getPOIitem(0).getPoint(), true);
        mPoiDataOverlay.selectPOIitem(mPOIdata.getPOIitem(0), true);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_search_mode:
                    mIsListMode = !mIsListMode;
                    mListView.setVisibility(mIsListMode ? View.VISIBLE : View.INVISIBLE);

                    if(mIsListMode)
                        mBtnSearchMode.setBackgroundResource(R.drawable.ico_map);
                    else
                        mBtnSearchMode.setBackgroundResource(R.drawable.ico_menu);

                    break;

                case R.id.btn_search:
                    getStores(keywordParser(mEditKeyword.getText().toString()));
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

    private final static int DELAY_SET_TRACKING_MODE = 5000;

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
                    mResourceProvider.getLocationDot();
                    break;

                case MSG_SORT_STORE_LIST:
                    sortStoreList();
                    break;

                default:
                    break;
            }
        }
    }
}
