package com.wowls.boddari.ui.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wowls.boddari.R;


public class TestStoreImageFragment extends Fragment
{
    private static final String LOG = "Goguma";

    private Context mContext;
    private Bitmap mImageBitmap;
    private ImageView mImageView;

    public TestStoreImageFragment(Context context, Bitmap bitmap)
    {
        mContext = context;
        mImageBitmap = bitmap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.search_info_store_image_fragment, container, false);
        mImageView = (ImageView) view.findViewById(R.id.image_store);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mImageView.setImageBitmap(mImageBitmap);
    }
}
