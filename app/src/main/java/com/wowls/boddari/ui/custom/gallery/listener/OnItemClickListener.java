package com.wowls.boddari.ui.custom.gallery.listener;


import com.wowls.boddari.ui.custom.gallery.adapter.GalleryAdapter;

/**
 * Created by woong on 2015. 10. 20..
 */
public interface OnItemClickListener {

    void OnItemClick(GalleryAdapter.PhotoViewHolder photoViewHolder, int position);
}
