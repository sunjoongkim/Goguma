package com.wowls.bottari.ui.search.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nhn.android.maps.NMapActivity;
import com.wowls.bottari.R;
import com.wowls.bottari.define.Define;
import com.wowls.bottari.retrofit.RetrofitService;
import com.wowls.bottari.service.GogumaService;
import com.wowls.bottari.ui.search.SearchFragment;

public class InfoFragmentOne extends Fragment
{
    private static final String LOG = "Goguma";

    private static InfoFragmentOne mMyFragment;
    private Context mContext;

    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private TextView mTextState;
    private TextView mTextScore;
    private TextView mTextAddress;
    private TextView mTextDistance;
    private TextView mTextDesc;
    private TextView mTextMenu;

    private static String mStoreInfo, mStoreDistance;

    public static InfoFragmentOne getInstance(String storeInfo, String storeDistance)
    {
        Bundle args = new Bundle();

        InfoFragmentOne fragment = new InfoFragmentOne();
        fragment.setArguments(args);

        Log.e(LOG, "==============> getInstance storeInfo : " + storeInfo);
        Log.e(LOG, "==============> getInstance storeDistance : " + storeDistance);

        mStoreInfo = storeInfo;
        mStoreDistance = storeDistance;

        return fragment;
    }

    public static InfoFragmentOne getFragment()
    {
        return mMyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.search_store_info_1, container, false);

        mMyFragment = this;

        mContext = getContext();
        mService = GogumaService.getService();

        mTextState = (TextView) view.findViewById(R.id.text_state);
        mTextScore = (TextView) view.findViewById(R.id.text_score);
        mTextAddress = (TextView) view.findViewById(R.id.text_address);
        mTextDistance = (TextView) view.findViewById(R.id.text_distance);
        mTextDesc = (TextView) view.findViewById(R.id.text_desc);
        mTextMenu = (TextView) view.findViewById(R.id.text_menu);

        storeParser(mStoreInfo);
        mTextDistance.setText(mStoreDistance);

        registerIntent();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroyView()
    {
        mContext.unregisterReceiver(mAddressReceiver);
        mMyFragment = null;
        super.onDestroyView();
    }

    private void registerIntent()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction("action_store_address");

        mContext.registerReceiver(mAddressReceiver, filter);
    }

    private BroadcastReceiver mAddressReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Log.e(LOG, "====================> onReceive action : " + action);

            if(action.equals("action_store_address"))
            {
                String address = intent.getStringExtra("store_address");

                Log.e(LOG, "====================> onReceive address : " + address);
                mTextAddress.setText(address);
            }
        }
    };

    private void storeParser(String json)
    {
        String menuList;
        String ratingList;
        String storeInfo;

        JsonParser parser = new JsonParser();
        JsonElement element = (JsonElement) parser.parse(json);

        if(!element.isJsonNull())
        {
            JsonObject object = element.getAsJsonObject();

            menuList = object.get("menuList").toString();
            ratingList = object.get("ratingList").toString();
            storeInfo = object.get("storeInfo").toString();

            Log.i(LOG, "=========> menuList : " + menuList);
            Log.i(LOG, "=========> ratingList : " + ratingList);
            Log.i(LOG, "=========> storeInfo : " + storeInfo);

            storeInfoParser(storeInfo);
            scoreParser(ratingList);
            menuParser(menuList);
        }
    }

    private void storeInfoParser(String json)
    {
        JsonParser parser = new JsonParser();
        JsonElement element = (JsonElement) parser.parse(json);

        String lon;
        String lat;

        if(!element.isJsonNull())
        {
            JsonObject object = element.getAsJsonObject();

            lon = object.get(Define.KEY_STORE_LON).toString();
            lat = object.get(Define.KEY_STORE_LAT).toString();

            if(SearchFragment.getFragment() != null)
                SearchFragment.getFragment().getStoreAddress(Double.parseDouble(lon), Double.parseDouble(lat));

            if(object.get(Define.KEY_STORE_ENABLED).toString().replace("\"", "").equals("Y"))
                mTextState.setText("영업중");
            else
                mTextState.setText("영업종료");

            mTextDesc.setText(object.get(Define.KEY_STORE_DESC).toString().replace("\"", ""));
        }
    }

    private void scoreParser(String json)
    {
        mTextScore.setText("평점 4.6 (리뷰 3개)");
    }

    private void menuParser(String json)
    {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        mTextMenu.setText("");

        for(JsonElement element : array)
        {
            if(element.isJsonNull())
                return;

            JsonObject object = element.getAsJsonObject();

            mTextMenu.append(object.get(Define.KEY_MENU_NAME).toString().replace("\"", "") + " ");
            mTextMenu.append(object.get(Define.KEY_MENU_PRICE).toString() + "\n");
        }
    }

    public class FindAddressActivity extends NMapActivity
    {
        @Override
        protected void onCreate(Bundle bundle)
        {
            super.onCreate(bundle);

        }

        @Override
        public void findPlacemarkAtLocation(double v, double v1)
        {
            super.findPlacemarkAtLocation(v, v1);
        }
    }

}
