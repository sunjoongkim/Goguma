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
import android.widget.ImageView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wowls.goguma.R;
import com.wowls.goguma.data.StoreInfo;
import com.wowls.goguma.define.Define;
import com.wowls.goguma.retrofit.RetrofitService;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment
{
    private static final String LOG = "Goguma";

    private static final double MAX_DISTANCE = 1000000;

    private Context mContext;
    private LocationManager mLocationManager;
    private MapView mMapView;

    private EditText mEditKeyword;
    private ImageView mBtnSearch;

    private RetrofitService mRetrofitService;

    private ArrayList<StoreInfo> mStoreInfo = new ArrayList<>();

    private double mDistanceLatitude = MAX_DISTANCE;
    private double mDistanceLongitude = MAX_DISTANCE;
    private double mNearestLatitude;
    private double mNearestLongitude;

    private MapPoint mCurrentMapCenter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.consumer_main, container, false);
        Log.i(LOG, "==========================> SearchFragment onCreateView");

        initRetrofit();
        mContext = getContext();

        mMapView = new MapView(mContext);
        mMapView.setMapViewEventListener(mMapViewEventListener);

        ViewGroup mapViewContainer = (ViewGroup) view.findViewById(R.id.map_view);
        mapViewContainer.addView(mMapView);

        mEditKeyword = (EditText) view.findViewById(R.id.edit_keyword);
        mBtnSearch = (ImageView) view.findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(mOnClickListener);

        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        checkGpsState();
        requestPermission();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

//        initMapView();
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

        mMapView.removeAllPOIItems();

        for (StoreInfo info : mStoreInfo)
        {
            lon = info.getLongitude();
            lat = info.getLatitude();

            double longitude = Double.parseDouble(lon);
            double latitude = Double.parseDouble(lat);

            MapPOIItem item = new MapPOIItem();
            MapPoint point = MapPoint.mapPointWithGeoCoord(latitude, longitude);
            item.setItemName(info.getStoreName());
            item.setMapPoint(point);
            mMapView.addPOIItem(item);

            setNearestStore(latitude, longitude);
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
                        Log.i(LOG, "===============> getStore response is null");
                        return;
                    }

                    try {
                        String json = response.body().string();
                        Log.i(LOG, "===============> getStores : " + json);

                        if(response.body() == null)
                            retryDialog("점포 가져오기 실패");
                        else
                        {
                            storeParser(json);
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

    private void storeParser(String json)
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
            storeName = element.getAsJsonObject().get(Define.KEY_STORE_NAME).toString();
            longitude = element.getAsJsonObject().get(Define.KEY_STORE_LON).toString();
            latitude = element.getAsJsonObject().get(Define.KEY_STORE_LAT).toString();
            storeId = element.getAsJsonObject().get(Define.KEY_STORE_ID).toString();
            ownerId = element.getAsJsonObject().get(Define.KEY_OWNER_ID).toString();

            Log.i(LOG, "=========> storeName : " + storeName);
            Log.i(LOG, "=========> longitude : " + longitude);
            Log.i(LOG, "=========> latitude : " + latitude);
            Log.i(LOG, "=========> storeId : " + storeId);
            Log.i(LOG, "=========> ownerId : " + ownerId);

            StoreInfo info = new StoreInfo(storeName, longitude, latitude, storeId, ownerId);
            mStoreInfo.add(info);
        }

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
