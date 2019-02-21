package com.wowls.boddari.ui.search.info;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wowls.boddari.R;
import com.wowls.boddari.ui.search.adapter.StoreInfoAdapter;

public class InfoFragmentOne extends Fragment
{
    private static final String LOG = "Goguma";

    private static InfoFragmentOne mMyFragment;

    private RecyclerView mRecyclerView;

    private static String mStoreInfo, mStoreDistance;

    public static InfoFragmentOne getInstance(String storeInfo, String storeDistance)
    {
        Bundle args = new Bundle();

        InfoFragmentOne fragment = new InfoFragmentOne();
        fragment.setArguments(args);

        mStoreInfo = storeInfo;
        mStoreDistance = storeDistance;

        return fragment;
    }

    public static InfoFragmentOne getFragment()
    {
        return mMyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.search_store_info_1, container, false);

        mMyFragment = this;

        mRecyclerView = (RecyclerView) view.findViewById(R.id.store_info_1_view);
        mRecyclerView.setAdapter(new StoreInfoAdapter(mStoreInfo, mStoreDistance));

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroyView()
    {
        mMyFragment = null;
        super.onDestroyView();
    }
}
