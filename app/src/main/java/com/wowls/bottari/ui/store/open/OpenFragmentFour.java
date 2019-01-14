package com.wowls.bottari.ui.store.open;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.wowls.bottari.R;
import com.wowls.bottari.retrofit.RetrofitService;
import com.wowls.bottari.service.GogumaService;

public class OpenFragmentFour extends Fragment
{
    private static final String LOG = "Goguma";

    private static OpenFragmentFour mMyFragment;
    private Context mContext;

    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private EditText mEditStoreDesc;

    public static OpenFragmentFour getInstance()
    {
        Bundle args = new Bundle();

        OpenFragmentFour fragment = new OpenFragmentFour();
        fragment.setArguments(args);

        return fragment;
    }

    public static OpenFragmentFour getFragment()
    {
        return mMyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.store_open_4, container, false);

        mMyFragment = this;

        mContext = getContext();
        mService = GogumaService.getService();

        mEditStoreDesc = (EditText) view.findViewById(R.id.edit_store_desc);

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

    public void saveStoreDesc()
    {
        if(mService != null)
            mService.setOpenStoreDesc(mEditStoreDesc.getText().toString());
    }

}
