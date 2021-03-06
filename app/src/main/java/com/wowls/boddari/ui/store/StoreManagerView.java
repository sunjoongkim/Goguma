package com.wowls.boddari.ui.store;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wowls.boddari.R;
import com.wowls.boddari.data.MenuInfo;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.custom.RemoveScrollMapView;
import com.wowls.boddari.ui.custom.StoreImageFragment;
import com.wowls.boddari.ui.store.adapter.MenuListAdapter;
import com.wowls.boddari.ui.store.adapter.StoreManagerImageAdapter;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreManagerView
{
    public final static String LOG = "Goguma";

    private Context mContext;
    private FragmentManager mFragmentManager;
    private View mMyView;
    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private RemoveScrollMapView mMapView;
    private ViewGroup mMapViewContainer;

    private double mLatitude;
    private double mLongitude;

    private ScrollView mScrollView;
    private TextView mTextStoreName;
    private EditText mTextStoreDesc;
    private TextView mTextStoreMenu;
    private Button mBtnRegist;
    private ImageView mBtnAddMenu;
    private Button mBtnRemove;

    private ViewPager mImageViewPager;

    private RecyclerView mListView;
    private MenuListAdapter mListAdapter;
    private StoreManagerImageAdapter mPagerAdapter;

    private ArrayList<MenuInfo> mMenuList;

    private MapPoint mCurrentLocation;

    private AlertDialog mEditDialog;
    private EditText mEditMenu;
    private EditText mEditPrice;
    private Button mBtnSubmit;
    private double mStoreLongitude;
    private double mStoreLatitude;

    public StoreManagerView(Context context, FragmentManager manager, View view, RetrofitService service)
    {
        mContext = context;
        mFragmentManager = manager;
        mMyView = view;
        mRetrofitService = service;

        mScrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        mImageViewPager = (ViewPager) view.findViewById(R.id.view_pager_store_image);
        mTextStoreName = (TextView) view.findViewById(R.id.text_manage_store_name);
        mTextStoreDesc = (EditText) view.findViewById(R.id.edit_manage_store_desc);
        mTextStoreMenu = (TextView) view.findViewById(R.id.text_manage_store_menu);
        mBtnRegist = (Button) view.findViewById(R.id.btn_enter);
        mBtnRegist.setOnClickListener(mOnClickListener);
        mBtnAddMenu = (ImageView) view.findViewById(R.id.btn_add_menu);
        mBtnAddMenu.setOnClickListener(mOnClickListener);
        mBtnRemove = (Button) view.findViewById(R.id.btn_remove_store);
        mBtnRemove.setOnClickListener(mOnClickListener);

        initMap();
    }

    public void setVisible(boolean visible)
    {
        mMyView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);

        if(visible)
        {
            mStoreManagerHandler.sendEmptyMessage(MSG_INIT_STORE_IMAGE);
//            initStoreImage(null);
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

    public void setStoreLongitude(String longitude)
    {
        mStoreLongitude = Double.parseDouble(longitude);
    }

    public void setStoreLatitude(String latitude)
    {
        mStoreLatitude = Double.parseDouble(latitude);
    }

    public void initStoreImage(ArrayList<Bitmap> bitmapList)
    {
        List<StoreImageFragment> fragment = new ArrayList<>();

        if(bitmapList != null)
        {
            for(Bitmap bitmap : bitmapList)
            {
                if(bitmap != null)
                    fragment.add(new StoreImageFragment(mContext, bitmap));
            }
        }

        mPagerAdapter = new StoreManagerImageAdapter(mFragmentManager, fragment);
        mImageViewPager.setAdapter(mPagerAdapter);
    }

    private MapView.MapViewEventListener mMapViewEventListener = new MapView.MapViewEventListener()
    {
        @Override
        public void onMapViewInitialized(MapView mapView)
        {
            Log.i(LOG, "==================> onMapViewInitialized");
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
            mMapView.setCurrentLocationEventListener(mCurrentLocationEventListener);

            if(mService != null)
            {
                MapPoint point = MapPoint.mapPointWithGeoCoord(mStoreLatitude, mStoreLongitude);

                MapPOIItem item = new MapPOIItem();
                item.setItemName(mService.getCurrentUser());
                item.setMapPoint(point);
                mMapView.setMapCenterPoint(point, true);
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
//                case R.id.btn_enter:
//                    // 서버로 위치 전송
//                    notifyMyPlace();
//                    break;
//
//                case R.id.btn_add_menu:
//                    editDialog();
//                    break;
//
//
//                case R.id.btn_submit:
//                    String menu = mEditMenu.getText().toString();
//                    String price = mEditPrice.getText().toString();
//
//                    registMenu(menu, price);
//
//                    MenuInfo info = new MenuInfo(menu, price);
//                    mMenuList.add(info);
//
//                    if(mListAdapter == null)
//                        initListAdapter();
//                    else
//                        mListAdapter.notifyItemInserted(mMenuList.size() - 1);
//
//                    mEditDialog.dismiss();
//                    break;

                case R.id.btn_remove_store:
                    removeDialog();
                    break;
            }
        }
    };

    private MapView.CurrentLocationEventListener mCurrentLocationEventListener = new MapView.CurrentLocationEventListener()
    {
        @Override
        public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v)
        {
            mCurrentLocation = mapPoint;

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
    };

    private void notifyMyPlace()
    {
        HashMap<String, String> map = new HashMap<>();

//        map.put("storeId", mService.getCurrentUser());
//        map.put("ownerId", mService.getCurrentUser());
//        map.put("storeName", mService.getCurrentUser());
//        map.put("storeDesc", mService.getCurrentUser() + " store");
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

    private void removeStore()
    {
        if(mRetrofitService != null && mService != null)
        {
            mRetrofitService.removeOwnStoreInfo(mService.getCurrentUser(), mService.getCurrentUser()).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    Log.i(LOG, "removeStore : " + response.body());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {

                }
            });
        }
    }

    public void registMenu(String name, String price)
    {
        HashMap<String, Object> map = new HashMap<>();

        map.put("storeId", mService.getCurrentUser());
        map.put("menuName", name);
        map.put("menuPrice", price);

        if(mRetrofitService != null && mService != null)
        {
            mRetrofitService.saveMenuInfo(mService.getCurrentUser(), mService.getCurrentUser(), map).enqueue(new Callback<ResponseBody>()
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

    private void removeDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("스토어를 삭제하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        removeStore();

                        if(mService != null)
                            mService.setExistStore(false);

                        if(mStoreChangeListener != null)
                            mStoreChangeListener.onRemoveStore();
                    }
                })
                .setNegativeButton("취소", null)
                .create()
                .show();
    }

    private void retryDialog(String comment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(comment)
                .setNegativeButton("다시 시도", null)
                .create()
                .show();
    }

    private StoreChangeListener mStoreChangeListener;

    public void setStoreChangeListener(StoreChangeListener listener)
    {
        mStoreChangeListener = listener;
    }

    public interface StoreChangeListener
    {
        void onRemoveStore();
    }

    public final static int MSG_SET_TRACKING_MODE = 1000;
    public final static int MSG_GET_LOCATION_DOT = MSG_SET_TRACKING_MODE + 1;
    public final static int MSG_INIT_STORE_IMAGE = MSG_GET_LOCATION_DOT + 1;

    private final static int DELAY_SET_TRACKING_MODE = 5000;

    private StoreManagerHandler mStoreManagerHandler = new StoreManagerHandler();

    private class StoreManagerHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {

                case MSG_INIT_STORE_IMAGE:
//                    initStoreImage(null);
                    break;

                default:
                    break;
            }
        }
    }
}

