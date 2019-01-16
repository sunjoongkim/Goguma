package com.wowls.bottari.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wowls.bottari.R;
import com.wowls.bottari.data.StoreInfo;
import com.wowls.bottari.define.Define;
import com.wowls.bottari.retrofit.RetrofitService;
import com.wowls.bottari.ui.search.info.SearchInfoActivity;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchViewHolder>
{
    private static final String LOG = "Goguma";

    private ArrayList<StoreInfo> mStoreList;
    private Context mContext;

    private RetrofitService mRetrofitService;

    public SearchListAdapter(ArrayList<StoreInfo> list, Context context, RetrofitService service)
    {
        mStoreList = list;
        mContext = context;
        mRetrofitService = service;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_list_item_view, viewGroup, false);
        SearchViewHolder holder = new SearchViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder viewHolder, final int position)
    {
        getStoreInfo(position, viewHolder);
    }

    @Override
    public int getItemCount()
    {
        return mStoreList.size();
    }

    private void getStoreInfo(int position, final SearchViewHolder viewHolder)
    {
        getStoreDistance(position, viewHolder);

        if(mRetrofitService != null)
        {
            mRetrofitService.showStoreInfo(mStoreList.get(position).getStoreId()).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    if(response.body() == null)
                    {
                        //                        retryDialog("점포 정보 가져오기 실패");
                        return;
                    }

                    try {
                        String json = response.body().string();
                        Log.i(LOG, "=====================> getStoreInfo : " + json);
                        viewHolder.currentStoreInfo = json;
                        storeParser(json, viewHolder);
                    }
                    catch (IOException e) {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {

                }
            });
        }
    }

    private void getStoreDistance(int position, SearchViewHolder viewHolder)
    {
        String distance;
        double distanceNum = mStoreList.get(position).getDistance();

        if(distanceNum >= 1000)
        {
            distanceNum = Double.parseDouble(String.format("%.1f", distanceNum / 1000));
            distance = String.valueOf(distanceNum) + "Km";
        }
        else
        {
            distanceNum = Double.parseDouble(String.format("%.0f", distanceNum));
            distance = String.valueOf(distanceNum) + "m";
        }

        viewHolder.textStoreDistance.setText(distance);
        viewHolder.currentStoreDistance = distance;
    }

    public void storeParser(String json, SearchViewHolder viewHolder)
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

            storeInfoParser(storeInfo, viewHolder);
            menuParser(menuList, viewHolder);
        }
    }

    private void storeInfoParser(String json, SearchViewHolder viewHolder)
    {
        JsonParser parser = new JsonParser();
        JsonElement element = (JsonElement) parser.parse(json);

        if(!element.isJsonNull())
        {
            JsonObject object = element.getAsJsonObject();

            if(object.get(Define.KEY_STORE_NAME) != null)
                viewHolder.textStoreName.setText(object.get(Define.KEY_STORE_NAME).toString().replace("\"", ""));

            if(object.get(Define.KEY_STORE_ENABLED).toString().replace("\"", "").equals("Y"))
                viewHolder.textStoreState.setText("open");
            else
                viewHolder.textStoreState.setText("close");

        }
    }

    private void menuParser(String json, SearchViewHolder viewHolder)
    {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        viewHolder.textStoreMenu.setText("");

        for(JsonElement element : array)
        {
            if(element.isJsonNull())
                return;

            JsonObject object = element.getAsJsonObject();

            viewHolder.textStoreMenu.append(object.get(Define.KEY_MENU_NAME).toString().replace("\"", "") + " ");
            viewHolder.textStoreMenu.append(object.get(Define.KEY_MENU_PRICE).toString() + "\n");
        }

    }

    public class SearchViewHolder extends RecyclerView.ViewHolder
    {
        protected FrameLayout viewItem;
        protected TextView textStoreName;
        protected TextView textStoreState;
        protected TextView textStoreDistance;
        protected ImageView imageStore;
        protected TextView textStoreMenu;

        protected String currentStoreInfo;
        protected String currentStoreDistance;

        public SearchViewHolder(@NonNull View view)
        {
            super(view);

            viewItem = (FrameLayout) view.findViewById(R.id.view_item);
            viewItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext, SearchInfoActivity.class);
                    intent.putExtra("store_info", currentStoreInfo);
                    intent.putExtra("store_distance", currentStoreDistance);
                    mContext.startActivity(intent);
                }
            });
            textStoreName = (TextView) view.findViewById(R.id.text_store_name);
            textStoreState = (TextView) view.findViewById(R.id.text_store_state);
            textStoreDistance = (TextView) view.findViewById(R.id.text_store_distance);
            imageStore = (ImageView) view.findViewById(R.id.image_store);
            textStoreMenu = (TextView) view.findViewById(R.id.text_store_menu);
        }
    }
}
