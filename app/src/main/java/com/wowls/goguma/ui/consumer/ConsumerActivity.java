package com.wowls.goguma.ui.consumer;

import android.Manifest;
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
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.wowls.goguma.R;

public class ConsumerActivity extends FragmentActivity
{

    private GoogleMap mGoogleMap;
    private MapFragment mMapFragment;
    private LocationManager mLocationManager;
    private RelativeLayout mBoxMap;

    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumer_main);

        mBoxMap = (RelativeLayout) findViewById(R.id.box_map);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, mLocationListener);
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

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }
    };
}
