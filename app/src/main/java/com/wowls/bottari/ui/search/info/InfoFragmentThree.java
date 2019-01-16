package com.wowls.bottari.ui.search.info;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wowls.bottari.R;
import com.wowls.bottari.retrofit.RetrofitService;
import com.wowls.bottari.service.GogumaService;

public class InfoFragmentThree extends Fragment
{
    private static final String LOG = "Goguma";

    private static InfoFragmentThree mMyFragment;
    private Context mContext;

    private GogumaService mService;
    private RetrofitService mRetrofitService;


    public static InfoFragmentThree getInstance()
    {
        Bundle args = new Bundle();

        InfoFragmentThree fragment = new InfoFragmentThree();
        fragment.setArguments(args);

        return fragment;
    }

    public static InfoFragmentThree getFragment()
    {
        return mMyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.search_store_info_3, container, false);

        mMyFragment = this;

        mContext = getContext();
        mService = GogumaService.getService();

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
        mMyFragment = null;
        super.onDestroy();
    }

}
