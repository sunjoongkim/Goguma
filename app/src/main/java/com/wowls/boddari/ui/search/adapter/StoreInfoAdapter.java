package com.wowls.boddari.ui.search.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wowls.boddari.R;
import com.wowls.boddari.define.Define;
import com.wowls.boddari.retrofit.ReverseGeoService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoreInfoAdapter extends RecyclerView.Adapter<StoreInfoAdapter.StoreInfoVH>
{
    private static final String LOG = "Goguma";

    private ReverseGeoService mReverseGeoService;

    private TextView mTextState;
    private TextView mTextScore;
    private TextView mTextAddress;
    private TextView mTextDistance;
    private TextView mTextDesc;
    private TextView mTextMenu;

    private String mStoreInfo, mStoreDistance;
    private float mAvgRating;

    public StoreInfoAdapter(String storeInfo, String storeDistance)
    {
        mStoreInfo = storeInfo;
        mStoreDistance = storeDistance;
    }

    @NonNull
    @Override
    public StoreInfoVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_store_info_1_item, viewGroup, false);

        initRetrofit();

        mTextState = (TextView) view.findViewById(R.id.text_state);
        mTextScore = (TextView) view.findViewById(R.id.text_score);
        mTextAddress = (TextView) view.findViewById(R.id.text_address);
        mTextDistance = (TextView) view.findViewById(R.id.text_distance);
        mTextDesc = (TextView) view.findViewById(R.id.text_desc);
        mTextMenu = (TextView) view.findViewById(R.id.text_menu);

        storeParser(mStoreInfo);
        mTextDistance.setText(mStoreDistance);

        return new StoreInfoVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreInfoVH storeInfoVH, int i)
    {

    }

    @Override
    public int getItemCount()
    {
        return 1;
    }

    private void initRetrofit()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Define.URL_NAVER_API)
                .build();

        mReverseGeoService = retrofit.create(ReverseGeoService.class);
    }

    private void storeParser(String json)
    {
        String menuList;
        String reviewList;
        String storeInfo;

        JsonParser parser = new JsonParser();
        JsonElement element = (JsonElement) parser.parse(json);

        if(!element.isJsonNull())
        {
            JsonObject object = element.getAsJsonObject();

            menuList = object.get("menuList").toString();
            reviewList = object.get("reviewList").toString();
            storeInfo = object.get("storeInfo").toString();

            Log.i(LOG, "=========> menuList : " + menuList);
            Log.i(LOG, "=========> reviewList : " + reviewList);
            Log.i(LOG, "=========> storeInfo : " + storeInfo);

            storeInfoParser(storeInfo);
            reviewParser(reviewList);
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

            getStoreAddress(lat, lon);

            if(object.get(Define.KEY_STORE_ENABLED).toString().replace("\"", "").equals("Y"))
                mTextState.setText("영업중");
            else
                mTextState.setText("영업종료");

            mTextDesc.setText(object.get(Define.KEY_STORE_DESC).toString()
                    .replace("\"", "").replace("\\n", System.lineSeparator()));
        }
    }

    private void getStoreAddress(String lat, String lon)
    {
        String coord = lon + "," + lat;

        if(mReverseGeoService != null)
        {
            mReverseGeoService.getAddress(coord, "json").enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    if(response.body() == null)
                        return;
                    else
                    {
                        try {
                            String json = response.body().string();
                            addressParser(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {

                }
            });
        }
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

    private void reviewParser(String json)
    {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        int rating = 0;
        String avg = "0";

        for(JsonElement element : array)
        {
            if(element.isJsonNull())
                return;

            JsonObject object = element.getAsJsonObject();

            rating = rating + Integer.parseInt(object.get(Define.KEY_RATING).toString());
        }

        if(array.size() != 0)
        {
            mAvgRating = rating / array.size();
            avg = String.format("%.1f", mAvgRating);
        }

        mTextScore.setText("평점 " + avg + " (리뷰 " + array.size() + "개)");
    }

    private void addressParser(String json)
    {
        JsonParser parser = new JsonParser();

        String results;
        JsonElement element = (JsonElement) parser.parse(json);
        JsonObject object = element.getAsJsonObject();
        results = object.get("results").toString();

        String region;
        JsonArray array = (JsonArray) parser.parse(results);

        for(JsonElement element1 : array)
        {
            JsonObject object1 = element1.getAsJsonObject();
            region = object1.get("region").toString();

            String area1;
            String area2;
            String area3;
            JsonElement element2 = (JsonElement) parser.parse(region);
            JsonObject object2 = element2.getAsJsonObject();
            area1 = object2.get("area1").toString();
            area2 = object2.get("area2").toString();
            area3 = object2.get("area3").toString();

            mTextAddress.setText("");

            areaParser(area1);
            areaParser(area2);
            areaParser(area3);
        }
    }

    private void areaParser(String json)
    {
        JsonParser parser = new JsonParser();
        JsonElement element = (JsonElement) parser.parse(json);

        String area;

        JsonObject object = element.getAsJsonObject();

        area = object.get("name").toString().replace("\"", "");

        mTextAddress.append(area + " ");
    }

    public class StoreInfoVH extends RecyclerView.ViewHolder
    {

        public StoreInfoVH(@NonNull View itemView)
        {
            super(itemView);
        }
    }
}
