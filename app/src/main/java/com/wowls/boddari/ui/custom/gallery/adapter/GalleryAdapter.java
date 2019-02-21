package com.wowls.boddari.ui.custom.gallery.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.wowls.boddari.R;
import com.wowls.boddari.ui.custom.gallery.listener.OnItemClickListener;
import com.wowls.boddari.ui.custom.gallery.vo.PhotoVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by woong on 2015. 10. 20..
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {


    private Activity mActivity;

    private int itemLayout;
    private List<PhotoVO> mPhotoList;

    private OnItemClickListener onItemClickListener;


    /**
     * PhotoList 반환
     * @return
     */
    public List<PhotoVO> getmPhotoList() {
        return mPhotoList;
    }


    /**
     * 선택된 PhotoList 반환
     * @return
     */
    public List<PhotoVO> getSelectedPhotoList(){

        List<PhotoVO> mSelectPhotoList = new ArrayList<>();

        for (int i = 0; i < mPhotoList.size(); i++) {

            PhotoVO photoVO = mPhotoList.get(i);
            if(photoVO.isSelected()){
                mSelectPhotoList.add(photoVO);
            }
        }

        return mSelectPhotoList;
    }

    /**
     * 아이템 선택시 호출되는 리스너
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    /**
     * 생성자
     * @param photoList
     * @param itemLayout
     */
    public GalleryAdapter(Activity activity, List<PhotoVO> photoList, int itemLayout) {

        mActivity = activity;

        this.mPhotoList = photoList;
        this.itemLayout = itemLayout;

    }




    /**
     * 레이아웃을 만들어서 Holer에 저장
     *
     * @param viewGroup
     * @param viewType
     * @return
     */
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new PhotoViewHolder(view);
    }


    /**
     * listView getView 를 대체
     * 넘겨 받은 데이터를 화면에 출력하는 역할
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(final PhotoViewHolder viewHolder, final int position) {

        final PhotoVO photoVO = mPhotoList.get(position);

        Glide.with(mActivity)
                .load(photoVO.getImgPath())
                .apply(new RequestOptions().centerCrop())
                .transition(new DrawableTransitionOptions().crossFade())
                .into(viewHolder.imgPhoto);

        //선택
        if(photoVO.isSelected()){
            viewHolder.layoutSelect.setVisibility(View.VISIBLE);
        }else{
            viewHolder.layoutSelect.setVisibility(View.INVISIBLE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(viewHolder, position);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }


    /**
     * 뷰 재활용을 위한 viewHolder
     */
    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgPhoto;
        public RelativeLayout layoutSelect;

        public PhotoViewHolder(View itemView) {
            super(itemView);

            imgPhoto = (ImageView) itemView.findViewById(R.id.imgPhoto);
            layoutSelect = (RelativeLayout) itemView.findViewById(R.id.layoutSelect);
        }

    }
}

