package com.wowls.goguma.ui.producer;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.wowls.goguma.R;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.service.GogumaService;

public class RegistMenuView
{
    private Context mContext;
    private View mMyView;
    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private Button mBtnAddMenu;

    public RegistMenuView(Context context, View view, RetrofitService service)
    {
        mContext = context;
        mMyView = view;
        mRetrofitService = service;

        mBtnAddMenu = (Button) view.findViewById(R.id.btn_enter);
        mBtnAddMenu.setOnClickListener(mOnClickListener);
    }

    public void setVisible(boolean visible)
    {
        mMyView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);

        if(visible)
        {
        }
    }

    public void setService(GogumaService service)
    {
        mService = service;
    }


    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_enter:
                    break;
            }
        }
    };
}
