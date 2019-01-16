package com.wowls.bottari.ui.etc.user;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wowls.bottari.R;
import com.wowls.bottari.define.ConnectionState;
import com.wowls.bottari.service.GogumaService;
import com.wowls.bottari.ui.MainActivity;
import com.wowls.bottari.ui.etc.EtcFragment;


public class EtcUserView
{
    public final static String LOG = "Goguma";

    private Context mContext;

    private View mMyView;
    private GogumaService mService;

    private TextView mTextUserId;
    private TextView mTextUserNick;

    private Button mBtnLogout;

    private String mUserId = "";
    private String mUserNick = "";

    private Handler mHandler;


    public EtcUserView(Context context, View view, Handler handler)
    {
        mContext = context;
        mMyView = view;
        mHandler = handler;
        mService = GogumaService.getService();

        mTextUserId = (TextView) view.findViewById(R.id.text_user_id);
        mTextUserNick = (TextView) view.findViewById(R.id.text_user_nick);
        mBtnLogout = (Button) view.findViewById(R.id.btn_logout);
        mBtnLogout.setOnClickListener(mOnClickListener);
    }

    public void setVisible(boolean visible)
    {
        mMyView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);

        if(visible)
        {
            mUserId = mService.getCurrentUser();
            mUserNick = mService.getCurrentUserNick();

            mTextUserId.setText(mUserId);
            mTextUserNick.setText(mUserNick);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.btn_logout:
                    if(mService != null)
                    {
                        mService.setConnectionState(ConnectionState.LOGOFF, "", "");
                        MainActivity.selectTab(1);
                    }

                    mHandler.sendEmptyMessage(EtcFragment.MSG_SUCCESS_LOGIN);
                    break;

                default:
                    break;
            }
        }
    };

}
