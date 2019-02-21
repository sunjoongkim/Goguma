package com.wowls.boddari.ui.search.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.wowls.boddari.R;
import com.wowls.boddari.data.StoreInfo;
import com.wowls.boddari.define.Define;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.search.info.SearchInfoActivity;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SearchPagerFragment extends Fragment
{
    private static final String LOG = "Goguma";

    private static final String POSITION = "position";
    private static final String SCALE = "scale";

    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private SearchPagerLayout mViewPager;

    private TextView mTextStoreName;
    private TextView mTextStoreState;
    private TextView mTextStoreDistance;
    private ImageView mImageStore;
    private TextView mTextStoreMenu;

    private String mCurrentStoreInfo;
    private String mCurrentStoreDistance;

    private static ArrayList<StoreInfo> mPageViewStoreInfo;

    public static Fragment getInstance(Context context, int pos, float scale)
    {
        Bundle bundle = new Bundle();

        bundle.putInt(POSITION, pos);
        bundle.putFloat(SCALE, scale);

        return Fragment.instantiate(context, SearchPagerFragment.class.getName(), bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(container == null)
            return null;

        mService = GogumaService.getService();
        initRetrofit();

        final int position = getArguments().getInt(POSITION);
        float scale = getArguments().getFloat(SCALE);

//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(300, 300);
//        layoutParams.setMargins(250, 0, 0, 0);
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.search_main_view_pager, container, false);

        mViewPager = (SearchPagerLayout) layout.findViewById(R.id.pager_layout);
        mViewPager.setOnClickListener(mOnClickListener);

        mTextStoreName = (TextView) layout.findViewById(R.id.text_store_name);
        mTextStoreState = (TextView) layout.findViewById(R.id.text_store_state);
        mTextStoreDistance = (TextView) layout.findViewById(R.id.text_store_distance);
        mImageStore = (ImageView) layout.findViewById(R.id.image_store);
        mTextStoreMenu = (TextView) layout.findViewById(R.id.text_store_menu);

        mViewPager.setScaleBoth(scale);
//        mViewPager.setAlpha(0.5f);

        getStoreInfo(position);

        return layout;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    private void initRetrofit()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Define.URL_BASE)
                .build();

        mRetrofitService = retrofit.create(RetrofitService.class);
    }

    public static void setPageViewStoreInfo(ArrayList<StoreInfo> fileInfo)
    {
        mPageViewStoreInfo = fileInfo;
    }

    private void getStoreInfo(int position)
    {
        Log.i(LOG, "=====================> getStoreInfo position : " + position);
        Log.i(LOG, "=====================> getStoreInfo storeId : " + mPageViewStoreInfo.get(position).getStoreId());

        getStoreDistance(position);

        if(mRetrofitService != null)
        {
            mRetrofitService.showStoreInfo(mPageViewStoreInfo.get(position).getStoreId()).enqueue(new Callback<ResponseBody>()
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
                        mCurrentStoreInfo = json;
                        storeParser(json);
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

    private void getStoreDistance(int position)
    {
        String distance;
        double distanceNum = mPageViewStoreInfo.get(position).getDistance();

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

        mTextStoreDistance.setText(distance);
        mCurrentStoreDistance = distance;
    }

    public void storeParser(String json)
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
            ratingList = object.get("reviewList").toString();
            storeInfo = object.get("storeInfo").toString();

            Log.i(LOG, "=========> menuList : " + menuList);
            Log.i(LOG, "=========> reviewList : " + ratingList);
            Log.i(LOG, "=========> storeInfo : " + storeInfo);

            storeInfoParser(storeInfo);
            menuParser(menuList);
        }
    }

    private void storeInfoParser(String json)
    {
        JsonParser parser = new JsonParser();
        JsonElement element = (JsonElement) parser.parse(json);

        if(!element.isJsonNull())
        {
            JsonObject object = element.getAsJsonObject();

            if(object.get(Define.KEY_STORE_NAME) != null)
                mTextStoreName.setText(object.get(Define.KEY_STORE_NAME).toString().replace("\"", ""));

            if(object.get(Define.KEY_STORE_ENABLED).toString().replace("\"", "").equals("Y"))
                mTextStoreState.setText("open");
            else
                mTextStoreState.setText("close");

            Log.i(LOG, "=========> mTextStoreName : " + mTextStoreName.getText());
            Log.i(LOG, "=========> mTextStoreState : " + mTextStoreState.getText());
        }
    }

    private void menuParser(String json)
    {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        mTextStoreMenu.setText("");

        for(JsonElement element : array)
        {
            if(element.isJsonNull())
                return;

            JsonObject object = element.getAsJsonObject();

            mTextStoreMenu.append(object.get(Define.KEY_MENU_NAME).toString().replace("\"", "") + " ");
            mTextStoreMenu.append(object.get(Define.KEY_MENU_PRICE).toString() + "\n");
        }

        Log.i(LOG, "=========> mTextStoreMenu : " + mTextStoreMenu.getText());
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(getActivity(), SearchInfoActivity.class);
            intent.putExtra("store_info", mCurrentStoreInfo);
            intent.putExtra("store_distance", mCurrentStoreDistance);
            getContext().startActivity(intent);
        }
    };

//    public void updateThumbnail(int position)
//    {
//        if(MusicService.isScanStarted())
//        {
//            GetThumbnailTask asyncTask = new GetThumbnailTask();
//            asyncTask.execute(position);
//        }
//        else
//        {
//            Uri thumbnailUri = mPageViewStoreInfo.get(position).getUriThumbnail();
//
//            if (thumbnailUri == null)
//                mThumbnail.setImageResource(R.drawable.thumbnail_default);
//            else
//                mThumbnail.setImageURI(thumbnailUri);
//        }
//    }
//
//    private class GetThumbnailTask extends AsyncTask<Integer, Void, Bitmap>
//    {
//
//        @Override
//        protected Bitmap doInBackground(Integer... position)
//        {
//            return getThumbnailFromRetriever(position[0]);
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap thumbnail)
//        {
//            super.onPostExecute(thumbnail);
//
//            if(thumbnail == null)
//                mThumbnail.setImageResource(R.drawable.thumbnail_default);
//            else
//                mThumbnail.setImageBitmap(thumbnail);
//        }
//    }
//
//    private Bitmap getThumbnailFromRetriever(int position)
//    {
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        Bitmap thumbnail;
//
//        try
//        {
//            retriever.setDataSource(mPageViewStoreInfo.get(position).getPath());
//
//            byte[] img = retriever.getEmbeddedPicture();
//            if(img != null)
//                thumbnail = BitmapFactory.decodeByteArray(img, 0, img.length);
//            else
//                thumbnail = null;
//        }
//        catch (Exception e)
//        {
//            Log.i(LOGTAG, "updateThumbnail Exception : " + e.toString());
//            thumbnail = null;
//        }
//
//        return thumbnail;
//    }


}
