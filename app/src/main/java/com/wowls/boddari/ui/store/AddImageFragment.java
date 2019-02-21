package com.wowls.boddari.ui.store;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wowls.boddari.R;
import com.wowls.boddari.ui.custom.gallery.CustomGalleryActivity;

import java.util.ArrayList;

public class AddImageFragment extends Fragment
{
    private static final String LOG = "Goguma";

    private static final int REQUEST_PERMISSIONS = 100;
    private Context mContext;

    private ArrayList<Uri> mImageList = new ArrayList<>();

    public static AddImageFragment getInstance()
    {
        Bundle args = new Bundle();

        AddImageFragment fragment = new AddImageFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.store_manage_add_image, container, false);
        mContext = getContext();
        
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setType("image/*");
//                getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), Define.PICTURE_REQUEST_CODE);

                checkPermission();

            }
        });

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private void checkPermission()
    {
        if ((ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        }else {
            Log.e("Else","Else");
            startGalleryActivity();
        }
    }

    private void startGalleryActivity()
    {
        Intent intent = new Intent(getActivity(), CustomGalleryActivity.class);
        startActivity(intent);
    }

}
