package com.wowls.goguma.ui.consumer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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

public class ConsumerActivity extends FragmentActivity
{
    private static final String LOG = "Goguma";

    private static final double MAX_DISTANCE = 1000000;

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

    private Location mCurrentLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumer_main);

        initRetrofit();

        mMapView = new MapView(this);
//        mMapView.setMapViewEventListener(mMapViewEventListener);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mMapView);

        mEditKeyword = (EditText) findViewById(R.id.edit_keyword);
        mBtnSearch = (ImageView) findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(mOnClickListener);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkGpsState();
        requestPermission();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        initMapView();
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
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            else
                mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        else
            mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private boolean checkPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void initMapView()
    {
        MapPoint point = MapPoint.mapPointWithGeoCoord(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mMapView.setMapCenterPoint(point, true);

        getStores(null);
    }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditKeyword.getWindowToken(), 0);

        mEditKeyword.setText("");
    }

    private void setNearestStore(double latitude, double longitude)
    {
        double distanceLatitude = Math.abs(mCurrentLocation.getLatitude() - latitude);
        double distanceLongitude = Math.abs(mCurrentLocation.getLongitude() - longitude);

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
}
