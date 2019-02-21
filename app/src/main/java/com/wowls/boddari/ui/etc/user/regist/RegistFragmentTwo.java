package com.wowls.boddari.ui.etc.user.regist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wowls.boddari.R;
import com.wowls.boddari.define.Define;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.service.GogumaService;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistFragmentTwo extends Fragment
{
    private static final String LOG = "Goguma";

    private static RegistFragmentTwo mMyFragment;
    private Context mContext;

    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private EditText mEditId;
    private EditText mEditNick;
    private EditText mEditPw;
    private EditText mEditPw2;
    private EditText mEditBirth;
    private RadioButton mBtnMale, mBtnFemale;
    private Button mBtnComplete;

    private String mUserId;
    private String mUserPw;
    private String mUserName;
    private String mUserType;

    public static RegistFragmentTwo getInstance()
    {
        Bundle args = new Bundle();

        RegistFragmentTwo fragment = new RegistFragmentTwo();
        fragment.setArguments(args);

        return fragment;
    }

    public static RegistFragmentTwo getFragment()
    {
        return mMyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.etc_user_regist_2, container, false);

        mMyFragment = this;

        mContext = getContext();
        mService = GogumaService.getService();
        initRetrofit();

        mEditId = (EditText) view.findViewById(R.id.edit_id_regist);
        mEditNick = (EditText) view.findViewById(R.id.edit_nick_regist);
        mEditPw = (EditText) view.findViewById(R.id.edit_password_regist);
        mEditPw2 = (EditText) view.findViewById(R.id.edit_password_2_regist);
        mEditBirth = (EditText) view.findViewById(R.id.edit_birth_regist);
        mBtnMale = (RadioButton) view.findViewById(R.id.btn_male);
        mBtnMale.setChecked(true);
        mBtnFemale = (RadioButton) view.findViewById(R.id.btn_female);
        mBtnComplete = (Button) view.findViewById(R.id.btn_complete);
        mBtnComplete.setOnClickListener(mOnClickListener);

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

    private void initRetrofit()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Define.URL_BASE)
                .build();

        mRetrofitService = retrofit.create(RetrofitService.class);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.btn_complete:
                    register();
                    break;

                default:
                    break;
            }
        }
    };

    private void register()
    {
        String userId = mEditId.getText().toString();
        String userNick = mEditNick.getText().toString();
        String userPw = mEditPw.getText().toString();
        String userPw2 = mEditPw2.getText().toString();

        if(isValidInfo(userId, userPw, userPw2))
        {
            HashMap<String, String> map = new HashMap<>();

            map.put("userId", userId);
            map.put("userPw", userPw);
            map.put("userName", userNick);
            map.put("userType", Define.TYPE_PRODUCER);

            if(mRetrofitService != null)
            {
                mRetrofitService.createUser(map).enqueue(new Callback<ResponseBody>()
                {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        Log.i(LOG, "register : " + response.body());

                        if(response.body() == null)
                            retryDialog("이미 등록된 아이디 입니다.");
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage("회원가입 성공.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            getActivity().finish();
                                        }
                                    })
                                    .create()
                                    .show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t)
                    {
                        Log.i(LOG, "onFailure : " + t.toString());
                    }
                });
            }
        }
    }

    private boolean isValidInfo(String userId, String userPw, String userPw2)
    {
        if(userId.equals(""))
        {
            retryDialog("아이디를 입력해 주세요.");
            return false;
        }
        else if(userId.contains(" "))
        {
            retryDialog("아이디에 공백이 있습니다.");
            return false;
        }
        else if(userId.contains("!") || userId.contains("@") || userId.contains("#") || userId.contains("$") ||
                userId.contains("%") || userId.contains("^") || userId.contains("&") || userId.contains("*") ||
                userId.contains("(") || userId.contains(")") || userId.contains("_") || userId.contains("+") ||
                userId.contains("-") || userId.contains("=") || userId.contains("{") || userId.contains("}") ||
                userId.contains("[") || userId.contains("]") || userId.contains(":") || userId.contains("\"") ||
                userId.contains(";") || userId.contains("\'") || userId.contains("<") || userId.contains(">") ||
                userId.contains("?") || userId.contains(",") || userId.contains(".") || userId.contains("/"))
        {
            retryDialog("아이디에 특수문자가 있습니다.");
            return false;
        }
        else if(userPw.equals(""))
        {
            retryDialog("비밀번호를 입력해 주세요.");
            return false;
        }
        else if(userPw.contains(" "))
        {
            retryDialog("비밀번호에 공백이 있습니다.");
            return false;
        }
        else if(!userPw.equals(userPw2))
        {
            retryDialog("비밀번호 확인과 일치하지 않습니다.");
            return false;
        }

        return true;
    }

    private void parseUserColumn(String json)
    {
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(json);

        mUserType = object.getAsJsonObject().get(Define.KEY_USER_TYPE).toString().replace("\"", "");
        mUserId = object.getAsJsonObject().get(Define.KEY_USER_ID).toString().replace("\"", "");
        mUserName = object.getAsJsonObject().get(Define.KEY_USER_NAME).toString().replace("\"", "");
        mUserPw = object.getAsJsonObject().get(Define.KEY_USER_PW).toString().replace("\"", "");

        Log.i(LOG, "===============> mUserType : " + mUserType);
        Log.i(LOG, "===============> mUserId : " + mUserId);
        Log.i(LOG, "===============> mUserName : " + mUserName);
        Log.i(LOG, "===============> mUserPw : " + mUserPw);

    }

    private void retryDialog(String comment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(comment)
                .setNegativeButton("다시 시도", mOnConfirmListener)
                .create()
                .show();
    }


    private DialogInterface.OnClickListener mOnConfirmListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
            mEditId.setText("");
            mEditId.requestFocus();
            mEditNick.setText("");
            mEditPw.setText("");
            mEditPw2.setText("");
        }
    };
}
