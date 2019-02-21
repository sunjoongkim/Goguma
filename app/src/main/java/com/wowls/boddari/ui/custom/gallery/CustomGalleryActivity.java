package com.wowls.boddari.ui.custom.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wowls.boddari.BoddariApplication;
import com.wowls.boddari.R;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.custom.gallery.adapter.GalleryAdapter;
import com.wowls.boddari.ui.custom.gallery.divider.GridDividerDecoration;
import com.wowls.boddari.ui.custom.gallery.listener.OnItemClickListener;
import com.wowls.boddari.ui.custom.gallery.manager.GalleryManager;
import com.wowls.boddari.ui.custom.gallery.vo.PhotoVO;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class CustomGalleryActivity extends AppCompatActivity
{
    private static final String LOG = "Goguma";

    private GalleryManager mGalleryManager;
    private RetrofitService mRetrofitService;
    private GogumaService mService;

    private ArrayList<PhotoVO> mPhotoList = new ArrayList<>();

    private RecyclerView mRecyclerGallery;
    private GalleryAdapter mGalleryAdapter;

    private ImageView mBtnBack;
    private TextView mBtnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_main);

        mRetrofitService = BoddariApplication.getInstance().getRetrofitService();
        mService = GogumaService.getService();

        initLayout();
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_done:
                selectDone();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 레이아웃 초기화
     */
    private void initLayout() {

        mRecyclerGallery = (RecyclerView) findViewById(R.id.recyclerGallery);
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(mOnClickListener);
        mBtnComplete = (TextView) findViewById(R.id.btn_complete);
        mBtnComplete.setOnClickListener(mOnClickListener);
    }


    /**
     * 데이터 초기화
     */
    private void init() {

        //갤러리 리사이클러뷰 초기화
        initRecyclerGallery();
    }


    /**
     * 갤러리 아미지 데이터 초기화
     */
    private List<PhotoVO> initGalleryPathList() {

        mGalleryManager = new GalleryManager(getApplicationContext());
        //return mGalleryManager.getDatePhotoPathList(2015, 9, 19);
        return mGalleryManager.getAllPhotoPathList();
    }


    /**
     * 확인 버튼 선택 시
     */
    private void selectDone() {

        List<PhotoVO> selectedPhotoList = mGalleryAdapter.getSelectedPhotoList();
        for (int i = 0; i < selectedPhotoList.size(); i++) {
            Log.i("", ">>> selectedPhotoList   :  " + selectedPhotoList.get(i).getImgPath());
        }
    }


    /**
     * 갤러리 리사이클러뷰 초기화
     */
    private void initRecyclerGallery() {

        mGalleryAdapter = new GalleryAdapter(CustomGalleryActivity.this, initGalleryPathList(), R.layout.gallery_item);
        mGalleryAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerGallery.setAdapter(mGalleryAdapter);
        mRecyclerGallery.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerGallery.setItemAnimator(new DefaultItemAnimator());
        mRecyclerGallery.addItemDecoration(new GridDividerDecoration(getResources(), R.drawable.divider_recycler_gallery));
    }


    /**
     * 리사이클러뷰 아이템 선택시 호출 되는 리스너
     */
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void OnItemClick(GalleryAdapter.PhotoViewHolder photoViewHolder, int position) {

            PhotoVO photoVO = mGalleryAdapter.getmPhotoList().get(position);

            if(photoVO.isSelected()){
                photoVO.setSelected(false);
                mPhotoList.remove(photoVO);
            }else{
                photoVO.setSelected(true);
                mPhotoList.add(photoVO);
            }

            mGalleryAdapter.getmPhotoList().set(position,photoVO);
            mGalleryAdapter.notifyDataSetChanged();
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_back:
                    finish();
                    break;

                case R.id.btn_complete:
                    uploadFile();
                    break;
            }
        }
    };

    private void uploadFile()
    {
        MultipartBody.Part[] uploadImage = new MultipartBody.Part[mPhotoList.size()];

        for(int i = 0; i < mPhotoList.size(); i++)
        {
//            File file = new File(mPhotoList.get(i).getImgPath());
//
//            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
//            uploadImage[i] = MultipartBody.Part.createFormData("imageArr", file.getName(), requestFile);
//
//            Log.i(LOG, "========> saveImageList file : " + file.getPath());

            try {
                File file = new File("data/data/com.wowls.boddari/files/image" + i + ".png");
                FileOutputStream fos = new FileOutputStream(file);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeFile(mPhotoList.get(i).getImgPath(), options);

                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fos);
                fos.close();

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                uploadImage[i] = MultipartBody.Part.createFormData("imageArr", file.getName(), requestFile);

                Log.i(LOG, "========> saveImageList file : " + file.getPath());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(mRetrofitService != null)
        {
            mRetrofitService.saveImageList(mService.getCurrentUser(), uploadImage).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response)
                {
                    Log.i(LOG, "========> saveImageList response : " + response.body());
                    finish();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {

                }
            });
        }
    }

}
