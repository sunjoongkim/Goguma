package com.wowls.boddari.ui.etc.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.wowls.boddari.BoddariApplication;
import com.wowls.boddari.R;
import com.wowls.boddari.define.ConnectionState;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.etc.EtcActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class EtcUserView
{
    public final static String LOG = "Goguma";

    private Context mContext;
    private EtcActivity mActivity;

    private View mMyView;
    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private ImageView mImageUserProfile;
    private TextView mTextUserId;
    private TextView mTextUserNick;

    private Button mBtnLogout;

    private String mUserId = "";
    private String mUserNick = "";
    private String mUserImage = "";

    private Handler mHandler;


    public EtcUserView(Context context, EtcActivity activity, View view, Handler handler)
    {
        mContext = context;
        mActivity = activity;
        mMyView = view;
        mHandler = handler;
        mService = GogumaService.getService();
        mRetrofitService = BoddariApplication.getInstance().getRetrofitService();

        mImageUserProfile = (ImageView) view.findViewById(R.id.image_profile);
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
            if(mService != null)
            {
                mUserId = mService.getCurrentUser();
                mUserNick = mService.getCurrentUserNick();
                mUserImage = mService.getCurrentUserImage();

                mTextUserId.setText(mUserId);
                mTextUserNick.setText(mUserNick);
                mImageUserProfile.setImageResource(R.drawable.bg_profile);

                if(mUserImage != null && !mUserImage.isEmpty())
                {
                    Glide.with(mContext)
                         .load(mUserImage)
                         .apply(new RequestOptions().circleCrop())
                         .into(mImageUserProfile);
                }

                GetProfileImageTask task = new GetProfileImageTask();
                task.execute();
            }
        }
    }

    private class GetProfileImageTask extends AsyncTask<Void, Bitmap, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(Void... voids)
        {
            Bitmap bitmap = null;
            try {
                if(mUserImage != null)
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(mUserImage).getContent());
                else
                    bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ico_map);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            super.onPostExecute(bitmap);

        }
    }

    private LogoutResponseCallback mLogoutCallback = new LogoutResponseCallback()
    {
        @Override
        public void onCompleteLogout()
        {
            if(mService != null)
                mService.setConnectionState(ConnectionState.LOGOFF, "", "", "");
        }
    };

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
                        mService.setConnectionState(ConnectionState.LOGOFF, "", "", "");
                        mActivity.finish();
                    }

                    UserManagement.getInstance().requestLogout(mLogoutCallback);
                    mHandler.sendEmptyMessage(EtcActivity.MSG_SUCCESS_LOGIN);
                    break;

                default:
                    break;
            }
        }
    };

}
