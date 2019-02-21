package com.wowls.boddari.ui.etc.user.regist;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.wowls.boddari.R;


public class RegistFragmentOne extends Fragment
{
    private static final String LOG = "Goguma";

    private static RegistFragmentOne mMyFragment;
    private Context mContext;

    private CheckBox mCheckProvision;
    private Button mBtnEmail, mBtnNaver, mBtnKakao, mBtnFacebook;

    public static RegistFragmentOne getInstance()
    {
        Bundle args = new Bundle();

        RegistFragmentOne fragment = new RegistFragmentOne();
        fragment.setArguments(args);

        return fragment;
    }

    public static RegistFragmentOne getFragment()
    {
        return mMyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.etc_user_regist_1, container, false);

        mMyFragment = this;

        mContext = getContext();

        mCheckProvision = (CheckBox) view.findViewById(R.id.check_provision);
        mBtnEmail = (Button) view.findViewById(R.id.btn_regist_email);
        mBtnEmail.setOnClickListener(mOnClickListener);
        mBtnNaver = (Button) view.findViewById(R.id.btn_regist_naver);
        mBtnNaver.setOnClickListener(mOnClickListener);
        mBtnKakao = (Button) view.findViewById(R.id.btn_regist_kakao);
        mBtnKakao.setOnClickListener(mOnClickListener);
        mBtnFacebook = (Button) view.findViewById(R.id.btn_regist_facebook);
        mBtnFacebook.setOnClickListener(mOnClickListener);

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

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(!mCheckProvision.isChecked())
            {
                retryDialog("약관에 동의해주세요.");
                return;
            }

            switch (v.getId())
            {
                case R.id.btn_regist_email:
                    EtcUserRegistActivity.selectTab(1);
                    break;

                case R.id.btn_regist_naver:
                    break;

                case R.id.btn_regist_kakao:
                    break;

                case R.id.btn_regist_facebook:
                    break;
            }
        }
    };

    private void retryDialog(String comment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(comment)
                .setNegativeButton("다시 시도", null)
                .create()
                .show();
    }
}
