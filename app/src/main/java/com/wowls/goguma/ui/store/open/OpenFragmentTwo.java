package com.wowls.goguma.ui.store.open;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wowls.goguma.R;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.service.GogumaService;
import com.wowls.goguma.ui.custom.RemoveScrollMapView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class OpenFragmentTwo extends Fragment
{
    private static final String LOG = "Goguma";

    private Context mContext;

    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private RemoveScrollMapView mMapView;
    private ViewGroup mMapViewContainer;

    public static OpenFragmentTwo getInstance()
    {
        Bundle args = new Bundle();

        OpenFragmentTwo fragment = new OpenFragmentTwo();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.store_open_2, container, false);

        mContext = getContext();
        mService = GogumaService.getService();

        mMapView = new RemoveScrollMapView(mContext);
        mMapView.setMapViewEventListener(mMapViewEventListener);

        mMapViewContainer = (ViewGroup) view.findViewById(R.id.map_view_open);
        mMapViewContainer.addView(mMapView);

        return view;
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

            if(mService != null)
            {
                mService.setLatitude(mapPoint.getMapPointGeoCoord().latitude);
                mService.setLongitude(mapPoint.getMapPointGeoCoord().longitude);

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
