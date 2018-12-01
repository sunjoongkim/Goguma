package com.wowls.goguma.ui.producer;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wowls.goguma.R;

public class NotiPlaceView
{
    private Context mContext;
    private FragmentActivity mFragmentActivity;
    private View mMyView;

    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private RelativeLayout mBoxMap;

    private double mLatitude;
    private double mLongitude;

    private Button mBtnRegist;

    public NotiPlaceView(Context context, FragmentActivity activity, View view)
    {
        mContext = context;
        mFragmentActivity = activity;
        mMyView = view;

        mBoxMap = (RelativeLayout) view.findViewById(R.id.box_map);
        mBtnRegist = (Button) view.findViewById(R.id.btn_enter);
        mBtnRegist.setOnClickListener(mOnClickListener);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setVisible(boolean visible)
    {
        mMyView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);

        if(visible)
        {
            checkGpsState();
            requestPermission();
        }
    }


    // GPS on/off 체크
    private void checkGpsState()
    {
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            mContext.startActivity(intent);
        }
    }

    // 위치관련 permission 체크
    private void requestPermission()
    {
        // 마쉬멜로 이상일 경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkPermission())
                ActivityCompat.requestPermissions(mFragmentActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
        if(ContextCompat.checkSelfPermission(mFragmentActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mFragmentActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
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

            SupportMapFragment mapFragment = (SupportMapFragment) mFragmentActivity.getSupportFragmentManager().findFragmentById(R.id.map);
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
            mGoogleMap.setOnMapClickListener(mOnMapClickListener);
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            LatLng position = new LatLng(mLatitude, mLongitude);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));

            MarkerOptions options = new MarkerOptions();
            options.position(position);
            mGoogleMap.addMarker(options);
        }
    };

    private GoogleMap.OnMapClickListener mOnMapClickListener = new GoogleMap.OnMapClickListener()
    {
        @Override
        public void onMapClick(LatLng latLng)
        {
            mLatitude = latLng.latitude;
            mLongitude = latLng.longitude;

            mGoogleMap.clear();

            MarkerOptions options = new MarkerOptions();
            options.position(latLng);
            mGoogleMap.addMarker(options);
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_enter:
                    // 서버로 위치 전송코드 추가
                    Toast.makeText(mContext, "위치 등록 완료", Toast.LENGTH_SHORT).show();
                    setVisible(false);
                    break;
            }
        }
    };
}
