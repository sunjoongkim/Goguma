package com.wowls.boddari.ui.search.info;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wowls.boddari.R;
import com.wowls.boddari.data.StoreReviewInfo;
import com.wowls.boddari.define.Define;
import com.wowls.boddari.ui.search.adapter.StoreReviewAdapter;

import java.util.ArrayList;


public class InfoFragmentTwo extends Fragment
{
    private static final String LOG = "Goguma";

    private static InfoFragmentTwo mMyFragment;

    private RecyclerView mRecyclerView;
    private static String mStoreReview;

    public static InfoFragmentTwo getInstance(String storeReview)
    {
        Bundle args = new Bundle();

        InfoFragmentTwo fragment = new InfoFragmentTwo();
        fragment.setArguments(args);

        mStoreReview = storeReview;

        return fragment;
    }

    public static InfoFragmentTwo getFragment()
    {
        return mMyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.search_store_info_2, container, false);

        mMyFragment = this;

        mRecyclerView = (RecyclerView) view.findViewById(R.id.store_info_2_view);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        reviewParser(mStoreReview);

    }

    @Override
    public void onDestroy()
    {
        mMyFragment = null;
        super.onDestroy();
    }


    private void reviewParser(String json)
    {
        Log.e(LOG, "========================> reviewParser json : " + json);
        ArrayList<StoreReviewInfo> reviewList = new ArrayList<>();

        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        String writerId;
        int rating;
        String comment;
        String updDate;

        for(JsonElement element : array)
        {
            if(element.isJsonNull())
                return;

            JsonObject object = element.getAsJsonObject();

            writerId = object.get(Define.KEY_WRITER_ID).toString().replace("\"", "");
            rating = Integer.parseInt(object.get(Define.KEY_RATING).toString());
            updDate = object.get(Define.KEY_UPD_DATE).toString().replace("\"", "");
            comment = object.get(Define.KEY_COMMENT).toString().replace("\"", "");

            StoreReviewInfo reviewInfo = new StoreReviewInfo(writerId, rating, comment, updDate);
            reviewList.add(reviewInfo);
        }

        mRecyclerView.setAdapter(new StoreReviewAdapter(reviewList));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration()
        {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
            {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 100;
            }
        });
    }

}
