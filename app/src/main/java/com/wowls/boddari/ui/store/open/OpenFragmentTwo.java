package com.wowls.boddari.ui.store.open;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wowls.boddari.R;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.custom.RemoveScrollMapView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class OpenFragmentTwo extends Fragment
{
    private static final String LOG = "Goguma";

    private static OpenFragmentTwo mMyFragment;
    private Context mContext;

    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private RemoveScrollMapView mMapView;
    private ViewGroup mMapViewContainer;
    private boolean mIsInitMap = true;

    public static OpenFragmentTwo getInstance()
    {
        Bundle args = new Bundle();

        OpenFragmentTwo fragment = new OpenFragmentTwo();
        fragment.setArguments(args);

        return fragment;
    }

    public static OpenFragmentTwo getFragment()
    {
        return mMyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.store_open_2, container, false);

        mMyFragment = this;

        mContext = getContext();
        mService = GogumaService.getService();

        mMapView = new RemoveScrollMapView(mContext);
        mMapView.setMapViewEventListener(mMapViewEventListener);

        mMapViewContainer = (ViewGroup) view.findViewById(R.id.map_view_open);
        mMapViewContainer.addView(mMapView);

        return view;
    }

    @Override
    public void onDestroy()
    {
        mMyFragment = null;
        super.onDestroy();
    }

    private MapView.MapViewEventListener mMapViewEventListener = new MapView.MapViewEventListener()
    {
        @Override
        public void onMapViewInitialized(MapView mapView)
        {
            Log.i(LOG, "==================> onMapViewInitialized");
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);

            if(mService != null)
            {
                double latitude = mService.getCurrentLatitude();
                double longitude = mService.getCurrentLongitude();
                MapPoint point = MapPoint.mapPointWithGeoCoord(latitude, longitude);

                MapPOIItem item = new MapPOIItem();
                item.setItemName(mService.getCurrentUser());
                item.setMapPoint(point);

                mService.setOpenLatitude(point.getMapPointGeoCoord().latitude);
                mService.setOpenLongitude(point.getMapPointGeoCoord().longitude);

                mMapView.addPOIItem(item);
                mMapView.setMapCenterPoint(point, true);
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

            if(mService != null)
            {
                mService.setOpenLatitude(mapPoint.getMapPointGeoCoord().latitude);
                mService.setOpenLongitude(mapPoint.getMapPointGeoCoord().longitude);

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


}
