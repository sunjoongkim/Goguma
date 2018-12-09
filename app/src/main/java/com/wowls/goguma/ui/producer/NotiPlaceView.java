package com.wowls.goguma.ui.producer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wowls.goguma.R;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.service.GogumaService;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class NotiPlaceView
{
    public final static String LOG = "Goguma";

    private Context mContext;
    private FragmentActivity mFragmentActivity;
    private View mMyView;
    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private RelativeLayout mBoxMap;

    private double mLatitude;
    private double mLongitude;

    private Button mBtnRegist;

    public NotiPlaceView(Context context, FragmentActivity activity, View view, RetrofitService service)
    {
        mContext = context;
        mFragmentActivity = activity;
        mMyView = view;
        mRetrofitService = service;

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

    public void setService(GogumaService service)
    {
        mService = service;
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
                    // 서버로 위치 전송
                    notifyMyPlace();
                    break;
            }
        }
    };

    private void notifyMyPlace()
    {
        HashMap<String, String> map = new HashMap<>();

        map.put("storeId", "sun3");
        map.put("ownerId", mService.getCurrentUser());
        map.put("storeName", "sun");
        map.put("storeDesc", "sun's store2");
        map.put("storeLat", String.valueOf(mLatitude));
        map.put("storeLon", String.valueOf(mLongitude));

        if(mRetrofitService != null)
        {
            mRetrofitService.createStore(mService.getCurrentUser(), map).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response)
                {
                    Log.i(LOG, "register : " + response.body());

                    if(response.body() == null)
                        retryDialog("위치 등록 실패");
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("위치 등록 성공 : " + mLatitude + ", " + mLongitude)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        setVisible(false);
                                    }
                                })
                                .create()
                                .show();
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
}
