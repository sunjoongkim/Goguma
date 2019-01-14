package com.wowls.bottari.ui.store;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.wowls.bottari.R;
import com.wowls.bottari.ui.MainActivity;


public class GuideLoginView
{
    public final static String LOG = "Goguma";

    private Context mContext;

    private View mMyView;

    private Button mBtnMoveLoginView;


    public GuideLoginView(Context context, View view)
    {
        mContext = context;
        mMyView = view;

        mBtnMoveLoginView = (Button) view.findViewById(R.id.btn_move);
        mBtnMoveLoginView.setOnClickListener(mOnClickListener);
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
                case R.id.btn_move:
                    MainActivity.selectTab(2);
                    break;

                default:
                    break;
            }
        }
    };

}
