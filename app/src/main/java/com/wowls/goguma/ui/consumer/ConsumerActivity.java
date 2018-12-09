package com.wowls.goguma.ui.consumer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wowls.goguma.R;
import com.wowls.goguma.define.Define;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.store_info.StoreInfo;

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

    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private RelativeLayout mBoxMap;

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

        mBoxMap = (RelativeLayout) findViewById(R.id.box_map);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkGpsState();
        requestPermission();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        getStores();
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
            {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, mLocationListener);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 10, mLocationListener);
            }
        }
        else
        {
            if(!checkPermission())
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, mLocationListener);
        }

    }

    private boolean checkPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private LocationListener mLocationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            if(checkPermission())
                return;

            mLocationManager.removeUpdates(mLocationListener);

            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            Log.i(LOG, "===========> mLatitude : " + mLatitude);
            Log.i(LOG, "===========> mLongitude : " + mLongitude);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if(mapFragment != null)
                mapFragment.getMapAsync(mMapReadyCallback);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

        @Override
        public void onProviderEnabled(String provider)
        {

        }

        @Override
        public void onProviderDisabled(String provider)
        {

        }
    };

    private OnMapReadyCallback mMapReadyCallback = new OnMapReadyCallback()
    {
        @Override
        public void onMapReady(GoogleMap googleMap)
        {
            mGoogleMap = googleMap;
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            LatLng position = new LatLng(mLatitude, mLongitude);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));

            MarkerOptions options = new MarkerOptions();
            options.position(position);
            mGoogleMap.addMarker(options);

            addStoreMarker();
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

            LatLng position = new LatLng(latitude, longitude);

            MarkerOptions options = new MarkerOptions();
            options.position(position);
            mGoogleMap.addMarker(options);
        }
    }

    private void getStores()
    {
        if(mRetrofitService != null)
        {
            mRetrofitService.getStores().enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response)
                {
                    try {
                        String json = response.body().string();
                        Log.i(LOG, "===============> register : " + json);

                        if(response.body() == null)
                            retryDialog("점포 가져오기 실패");
                        else
                        {
                            storeParser(json);
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
