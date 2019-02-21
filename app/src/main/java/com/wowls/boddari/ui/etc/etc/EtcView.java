package com.wowls.boddari.ui.etc.etc;

import android.content.Context;
import android.view.View;

import com.wowls.boddari.R;


public class EtcView
{
    public final static String LOG = "Goguma";

    private Context mContext;

    private View mMyView;



    public EtcView(Context context, View view)
    {
        mContext = context;
        mMyView = view;
    }

    public void setVisible(boolean visible)
    {
        mMyView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.btn_login:
                    break;

                default:
                    break;
            }
        }
    };

}
