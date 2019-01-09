package com.wowls.goguma.ui.store;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wowls.goguma.R;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.service.GogumaService;
import com.wowls.goguma.ui.custom.RemoveScrollMapView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

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

    private RemoveScrollMapView mMapView;
    private ViewGroup mMapViewContainer;

    private double mLatitude;
    private double mLongitude;

    private Button mBtnRegist;

    public NotiPlaceView(Context context, FragmentActivity activity, View view, RetrofitService service)
    {
        mContext = context;
        mFragmentActivity = activity;
        mMyView = view;
        mRetrofitService = service;

        mBtnRegist = (Button) view.findViewById(R.id.btn_enter);
        mBtnRegist.setOnClickListener(mOnClickListener);
    }

    public void setVisible(boolean visible)
    {
        mMyView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);

//        if(visible)
//            initMap();
    }

    public void setService(GogumaService service)
    {
        mService = service;
    }

    public void initMap()
    {
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

    private MapView.MapViewEventListener mMapViewEventListener = new MapView.MapViewEventListener()
    {
        @Override
        public void onMapViewInitialized(MapView mapView)
        {
            Log.i(LOG, "==================> onMapViewInitialized");
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mMapView.setCurrentLocationEventListener(new MapView.CurrentLocationEventListener()
            {
                @Override
                public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v)
                {
                    mMapView.setCurrentLocationTrackingMode(null);

                }

                @Override
                public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v)
                {

                }

                @Override
                public void onCurrentLocationUpdateFailed(MapView mapView)
                {

                }

                @Override
                public void onCurrentLocationUpdateCancelled(MapView mapView)
                {

                }
            });

            if(mService != null)
            {
                MapPOIItem item = new MapPOIItem();
                item.setItemName(mService.getCurrentUser());
                item.setMapPoint(mMapView.getMapCenterPoint());
                mMapView.addPOIItem(item);
            }
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
            Log.i(LOG, "==================> onMapViewSingleTapped");
            mLatitude = mapPoint.getMapPointGeoCoord().latitude;
            mLongitude = mapPoint.getMapPointGeoCoord().longitude;

            if(mService != null)
            {
                mMapView.removeAllPOIItems();
                MapPOIItem item = new MapPOIItem();
                item.setItemName(mService.getCurrentUser());
                item.setMapPoint(mapPoint);
                mMapView.addPOIItem(item);
            }
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

        map.put("storeId", mService.getCurrentUser());
        map.put("ownerId", mService.getCurrentUser());
        map.put("storeName", mService.getCurrentUser());
        map.put("storeDesc", mService.getCurrentUser() + " store");
        map.put("storeLat", String.valueOf(mLatitude));
        map.put("storeLon", String.valueOf(mLongitude));

        if(mRetrofitService != null)
        {
            mRetrofitService.saveStoreInfo(mService.getCurrentUser(), map).enqueue(new Callback<ResponseBody>()
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
