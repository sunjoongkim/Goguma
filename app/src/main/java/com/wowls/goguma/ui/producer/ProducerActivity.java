package com.wowls.goguma.ui.producer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.wowls.goguma.R;

public class ProducerActivity extends FragmentActivity
{

    private NotiPlaceView mNotiPlaceView;

    private Button mBtnNoti, mBtnRegist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.producer_main);

        mNotiPlaceView = new NotiPlaceView(this, this, findViewById(R.id.notify_view));

        mBtnNoti = (Button) findViewById(R.id.btn_notify);
        mBtnNoti.setOnClickListener(mOnClickListener);
        mBtnRegist = (Button) findViewById(R.id.btn_regist);
        mBtnRegist.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_notify:
                    mNotiPlaceView.setVisible(true);
                    break;

                case R.id.btn_regist:
                    break;
            }
        }
    };
}
