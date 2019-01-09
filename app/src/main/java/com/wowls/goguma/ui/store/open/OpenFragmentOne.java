package com.wowls.goguma.ui.store.open;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.wowls.goguma.R;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.service.GogumaService;

public class OpenFragmentOne extends Fragment
{
    private static final String LOG = "Goguma";

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.store_open_1, container, false);

        mContext = getContext();
        mService = GogumaService.getService();

        mEditName = (EditText) view.findViewById(R.id.edit_store_name);
        mEditName.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(mService != null)
                    mService.setStoreName(v.getText().toString());

                return false;
            }
        });

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }
}
