package com.wowls.goguma.ui.search;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wowls.goguma.R;
import com.wowls.goguma.data.StoreInfo;
import com.wowls.goguma.define.Define;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.ui.custom.RemoveScrollMapView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment
{
    private static final String LOG = "Goguma";

    private static final double MAX_DISTANCE = 1000000;

    private static SearchFragment mMyFragment;

    private Context mContext;
    private LocationManager mLocationManager;
    private RemoveScrollMapView mMapView;
    private ViewGroup mMapViewContainer;
    private View mMyView;

    private EditText mEditKeyword;
    private ImageView mBtnSearch;

    private FrameLayout mStoreInfoView;
    private TextView mTextStoreName;
    private TextView mTextStoreState;
    private ImageView mImageStore;
    private TextView mTextStoreMenu;

    private RetrofitService mRetrofitService;

    private ArrayList<StoreInfo> mStoreInfo = new ArrayList<>();

    private double mDistanceLatitude = MAX_DISTANCE;
    private double mDistanceLongitude = MAX_DISTANCE;
    private double mNearestLatitude;
    private double mNearestLongitude;

    private MapPoint mCurrentMapCenter;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mMyView = inflater.inflate(R.layout.search_main, container, false);
        Log.i(LOG, "==========================> SearchFragment onCreateView");

        mMyFragment = this;

        initRetrofit();
        mContext = getContext();

        initMap();

        mEditKeyword = (EditText) mMyView.findViewById(R.id.edit_keyword);
        mBtnSearch = (ImageView) mMyView.findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(mOnClickListener);

        mStoreInfoView = (FrameLayout) mMyView.findViewById(R.id.view_store_info);
        mStoreInfoView.setOnClickListener(mOnClickListener);
        mTextStoreName = (TextView) mMyView.findViewById(R.id.text_store_name);
        mTextStoreState = (TextView) mMyView.findViewById(R.id.text_store_state);
        mImageStore = (ImageView) mMyView.findViewById(R.id.image_store);
        mTextStoreMenu = (TextView) mMyView.findViewById(R.id.text_store_menu);

        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        checkGpsState();
        requestPermission();

        return mMyView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onDestroy()
    {
        mMyFragment = null;
        super.onDestroy();
    }

    public void initMap()
    {
        Log.i(LOG, "=========================> @@@@@@@@@@@@@@@@@ initMap : " + mMapView);
        if(mMapView == null)
        {
            mMapView = new RemoveScrollMapView(mContext);
            mMapView.setMapViewEventListener(mMapViewEventListener);

            mMapViewContainer = (ViewGroup) mMyView.findViewById(R.id.map_view);
            mMapViewContainer.addView(mMapView);
        }
    }

    public void finishMap()
    {
        if(mMapView != null && mMapViewContainer != null)
        {
            mMapView.setVisibility(View.INVISIBLE);
            mMapViewContainer.removeAllViews();

            mMapView = null;
            mMapViewContainer = null;
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


    private MapView.MapViewEventListener mMapViewEventListener = new MapView.MapViewEventListener()
    {
        @Override
        public void onMapViewInitialized(MapView mapView)
        {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mCurrentMapCenter = mapView.getMapCenterPoint();

            getStores(null);

            Message msg = new Message();
            msg.what = MSG_SET_TRACKING_MODE;
            msg.obj = mapView;

            mSearchHandler.sendMessageDelayed(msg, DELAY_SET_TRACKING_MODE);
        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint)
        {

        }

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i)
        {

        }

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint)
        {

        }

        @Override
        public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint)
        {

        }

        @Override
        public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint)
        {

        }

        @Override
        public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint)
        {
            if(mStoreInfoView.getVisibility() == View.VISIBLE)
                mStoreInfoView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint)
        {

        }

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint)
        {

        }
    };

    private void addStoreMarker()
    {
        String lon;
        String lat;

        if(mMapView != null)
        {
            mMapView.removeAllPOIItems();

            for (StoreInfo info : mStoreInfo)
            {
                lon = info.getLongitude();
                lat = info.getLatitude();

                double longitude = Double.parseDouble(lon);
                double latitude = Double.parseDouble(lat);

                MapPOIItem item = new MapPOIItem();
                MapPoint point = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                item.setItemName(info.getStoreId());
                item.setMapPoint(point);
                mMapView.addPOIItem(item);
                mMapView.setPOIItemEventListener(mMarkerClickListener);

                setNearestStore(latitude, longitude);
            }
        }
    }

    private void getStores(final String[] keywords)
    {
        mDistanceLatitude = MAX_DISTANCE;
        mDistanceLongitude = MAX_DISTANCE;

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

                        storeListParser(json);
                        addStoreMarker();
                        initEditText();

                        if(keywords != null && keywords.length != 0)
                        {
                            if(mStoreInfo.isEmpty())
                                retryDialog("검색된 점포가 없습니다.");
                            else
                                mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(mNearestLatitude, mNearestLongitude), 2, true);
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

    private void getStoreInfo(MapPOIItem item)
    {
        Log.i(LOG, "=====================> getStoreInfo item : " + item.getItemName());
        String itemName = item.getItemName().replace("\"", "");

        if(mRetrofitService != null)
        {
            mRetrofitService.showStoreInfo(itemName).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    if(response.body() == null)
                    {
                        retryDialog("점포 정보 가져오기 실패");
                        return;
                    }

                    try {
                        String json = response.body().string();
                        Log.i(LOG, "=====================> getStoreInfo : " + json);
                        storeParser(json);
                    }
                    catch (IOException e) {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {

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
        if(!mStoreInfo.isEmpty())
            mStoreInfo.clear();

        String storeName;
        String longitude;
        String latitude;
        String storeId;
        String ownerId;

        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        for(JsonElement element : array)
        {
            if(element.isJsonNull())
                return;

            JsonObject object = element.getAsJsonObject();

            longitude = object.get(Define.KEY_STORE_LON).toString();
            latitude = object.get(Define.KEY_STORE_LAT).toString();
            storeId = object.get(Define.KEY_STORE_ID).toString();
            ownerId = object.get(Define.KEY_OWNER_ID).toString();
            if(object.get(Define.KEY_STORE_NAME) == null)
                storeName = storeId;
            else
                storeName = object.get(Define.KEY_STORE_NAME).toString();

            Log.i(LOG, "=========> longitude : " + longitude);
            Log.i(LOG, "=========> latitude : " + latitude);
            Log.i(LOG, "=========> storeId : " + storeId);
            Log.i(LOG, "=========> ownerId : " + ownerId);
            Log.i(LOG, "=========> storeName : " + storeName);

            StoreInfo info = new StoreInfo(storeName, longitude, latitude, storeId, ownerId);
            mStoreInfo.add(info);
        }
    }

    private void storeParser(String json)
    {
        String menuList;
        String ratingList;
        String storeInfo;

        JsonParser parser = new JsonParser();
        JsonElement element = (JsonElement) parser.parse(json);

        if(!element.isJsonNull())
        {
            JsonObject object = element.getAsJsonObject();

            menuList = object.get("menuList").toString();
            ratingList = object.get("ratingList").toString();
            storeInfo = object.get("storeInfo").toString();

            Log.i(LOG, "=========> menuList : " + menuList);
            Log.i(LOG, "=========> ratingList : " + ratingList);
            Log.i(LOG, "=========> storeInfo : " + storeInfo);

            storeInfoParser(storeInfo);
            menuParser(menuList);
        }
    }

    private void storeInfoParser(String json)
    {
        JsonParser parser = new JsonParser();
        JsonElement element = (JsonElement) parser.parse(json);

        if(!element.isJsonNull())
        {
            JsonObject object = element.getAsJsonObject();

            mTextStoreName.setText(object.get(Define.KEY_STORE_NAME).toString().replace("\"", ""));

            if(object.get(Define.KEY_STORE_ENABLED).toString().equals("Y"))
                mTextStoreState.setText("open");
            else
                mTextStoreState.setText("close");

            Log.i(LOG, "=========> mTextStoreName : " + mTextStoreName.getText());
            Log.i(LOG, "=========> mTextStoreState : " + mTextStoreState.getText());
        }
    }

    private void menuParser(String json)
    {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        mTextStoreMenu.setText("");

        for(JsonElement element : array)
        {
            if(element.isJsonNull())
                return;

            JsonObject object = element.getAsJsonObject();

            mTextStoreMenu.append(object.get(Define.KEY_MENU_NAME).toString().replace("\"", "") + " ");
            mTextStoreMenu.append(object.get(Define.KEY_MENU_PRICE).toString() + "\n");
        }

        Log.i(LOG, "=========> mTextStoreMenu : " + mTextStoreMenu.getText());
    }

    private void initEditText()
    {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditKeyword.getWindowToken(), 0);

        mEditKeyword.setText("");
    }

    private void setNearestStore(double latitude, double longitude)
    {
        double distanceLatitude = Math.abs(mCurrentMapCenter.getMapPointGeoCoord().latitude - latitude);
        double distanceLongitude = Math.abs(mCurrentMapCenter.getMapPointGeoCoord().longitude - longitude);

        Log.i(LOG, "================> distanceLatitude : " + distanceLatitude);
        Log.i(LOG, "================> distanceLongitude : " + distanceLongitude);

        if(mDistanceLatitude > distanceLatitude && mDistanceLatitude > distanceLongitude)
        {
            mDistanceLatitude = distanceLatitude;
            mDistanceLongitude = distanceLongitude;

            mNearestLatitude = latitude;
            mNearestLongitude = longitude;
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_search:
                    getStores(keywordParser(mEditKeyword.getText().toString()));
                    break;

                case R.id.view_store_info:
                    break;
            }
        }
    };

    private MapView.POIItemEventListener mMarkerClickListener = new MapView.POIItemEventListener()
    {
        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem)
        {
            getStoreInfo(mapPOIItem);
            mStoreInfoView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem)
        {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType)
        {

        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint)
        {

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

    public final static int MSG_SET_TRACKING_MODE = 1000;

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

                default:
                    break;
            }
        }
    }
}
