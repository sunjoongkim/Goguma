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

public class OpenFragmentOne extends Fragment
{
    private static final String LOG = "Goguma";

    private static OpenFragmentOne mMyFragment;
    private Context mContext;

    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private EditText mEditName;

    public static OpenFragmentOne getInstance()
    {
        Bundle args = new Bundle();

        OpenFragmentOne fragment = new OpenFragmentOne();
        fragment.setArguments(args);

        return fragment;
    }

    public static OpenFragmentOne getFragment()
    {
        return mMyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.store_open_1, container, false);

        mMyFragment = this;

        mContext = getContext();
        mService = GogumaService.getService();

        mEditName = (EditText) view.findViewById(R.id.edit_store_name);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(mService != null)
            mEditName.setText(mService.getCurrentUser() + " 의 스토어");
    }

    @Override
    public void onDestroy()
    {
        mMyFragment = null;
        super.onDestroy();
    }

    public boolean isEmptyStoreName()
    {
        if(mService != null)
            mService.setOpenStoreName(mEditName.getText().toString());

        return mEditName.getText().toString().isEmpty();
    }
}
