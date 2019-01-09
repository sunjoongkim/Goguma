package com.wowls.goguma.ui.store;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wowls.goguma.R;
import com.wowls.goguma.adapter.MenuListAdapter;
import com.wowls.goguma.data.MenuInfo;
import com.wowls.goguma.define.Define;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.service.GogumaService;
import com.wowls.goguma.ui.custom.RemoveScrollMapView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreManagerView
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

    private TextView mTextStoreName;
    private EditText mTextStoreDesc;
    private TextView mTextStoreMenu;
    private Button mBtnRegist;
    private ImageView mBtnAddMenu;

    private RecyclerView mListView;
    private MenuListAdapter mListAdapter;

    private ArrayList<MenuInfo> mMenuList;

    private AlertDialog mEditDialog;
    private EditText mEditMenu;
    private EditText mEditPrice;
    private Button mBtnSubmit;

    public StoreManagerView(Context context, FragmentActivity activity, View view, RetrofitService service)
    {
        mContext = context;
        mFragmentActivity = activity;
        mMyView = view;
        mRetrofitService = service;

        mTextStoreName = (TextView) view.findViewById(R.id.text_manage_store_name);
        mTextStoreDesc = (EditText) view.findViewById(R.id.edit_manage_store_desc);
        mTextStoreMenu = (TextView) view.findViewById(R.id.text_manage_store_menu);
        mBtnRegist = (Button) view.findViewById(R.id.btn_enter);
        mBtnRegist.setOnClickListener(mOnClickListener);
        mBtnAddMenu = (ImageView) view.findViewById(R.id.btn_add_menu);
        mBtnAddMenu.setOnClickListener(mOnClickListener);
    }

    public void setVisible(boolean visible)
    {
        mMyView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);

        if(visible)
        {
        }
    }

    public void setService(GogumaService service)
    {
        mService = service;
    }


    public void initMap()
    {
        if(mMapView == null)
        {
            Log.i(LOG, "============> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
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

    public void setTextStoreName(String name)
    {
        if(mTextStoreName != null)
            mTextStoreName.setText(name);
    }

    public void setTextStoreDesc(String desc)
    {
        if(mTextStoreDesc != null)
            mTextStoreDesc.setText(desc);
    }

    public void setTextStoreMenu(ArrayList<MenuInfo> menu)
    {
        if(mTextStoreMenu != null)
        {
            for(MenuInfo info : menu)
            {
                Log.i(LOG, "================> info.getMenuName : " + info.getMenuName());
                Log.i(LOG, "================> info.getMenuPrice : " + info.getMenuPrice());

                mTextStoreMenu.append(info.getMenuName() + "                  " + info.getMenuPrice() + "\n");
            }
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

                case R.id.btn_add_menu:
                    editDialog();
                    break;


                case R.id.btn_submit:
                    String menu = mEditMenu.getText().toString();
                    String price = mEditPrice.getText().toString();

                    registMenu(menu, price);

                    MenuInfo info = new MenuInfo(menu, price);
                    mMenuList.add(info);

                    if(mListAdapter == null)
                        initListAdapter();
                    else
                        mListAdapter.notifyItemInserted(mMenuList.size() - 1);

                    mEditDialog.dismiss();
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

    private void initListAdapter()
    {
        mListAdapter = new MenuListAdapter(mMenuList, mContext, false);
        mListView.setAdapter(mListAdapter);
    }

    public void registMenu(String name, String price)
    {
        HashMap<String, String> map = new HashMap<>();

        map.put(Define.KEY_MENU_STORE_ID, mService.getCurrentUser());
        map.put(Define.KEY_MENU_NAME, name);
        map.put(Define.KEY_MENU_PRICE, price);

        if(mRetrofitService != null && mService != null)
        {
            mRetrofitService.saveMenuInfo(mService.getCurrentUser(), map).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    Log.i(LOG, "register : " + response.body());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Log.i(LOG, "onFailure : " + t.toString());
                }
            });
        }
    }

    public void editDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        View view = LayoutInflater.from(mContext).inflate(R.layout.store_manage_menu_edit_dialog, null, false);
        builder.setView(view);

        mEditMenu = (EditText) view.findViewById(R.id.edit_menu_name);
        mEditPrice = (EditText) view.findViewById(R.id.edit_menu_price);
        mBtnSubmit = (Button) view.findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(mOnClickListener);

        mEditDialog = builder.create();
        mEditDialog.show();
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
