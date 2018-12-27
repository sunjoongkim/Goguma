package com.wowls.goguma.ui.consumer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wowls.goguma.R;
import com.wowls.goguma.define.Define;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.data.StoreInfo;

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

    private LocationManager mLocationManager;
    private MapView mMapView;

    private RetrofitService mRetrofitService;

    private ArrayList<StoreInfo> mStoreInfo = new ArrayList<>();

    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumer_main);

        initRetrofit();

        mMapView = new MapView(this);
        mMapView.setMapViewEventListener(mMapViewEventListener);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mMapView);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkGpsState();
        requestPermission();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
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
        }
    }

    private boolean checkPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private MapView.MapViewEventListener mMapViewEventListener = new MapView.MapViewEventListener()
    {
        @Override
        public void onMapViewInitialized(MapView mapView)
        {
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

            getStores();
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
        if(mStoreInfo.isEmpty())
            return;

        String lon;
        String lat;

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
        }
    }

    private void getStores()
    {
        if(mRetrofitService != null)
        {
            mRetrofitService.showStoreList().enqueue(new Callback<ResponseBody>()
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
}
