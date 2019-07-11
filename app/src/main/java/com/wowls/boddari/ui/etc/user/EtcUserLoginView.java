package com.wowls.boddari.ui.etc.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.util.exception.KakaoException;
import com.wowls.boddari.R;
import com.wowls.boddari.define.ConnectionState;
import com.wowls.boddari.define.Define;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.etc.EtcActivity;
import com.wowls.boddari.ui.etc.user.regist.EtcUserRegistActivity;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class EtcUserLoginView
{
    public final static String LOG = "Goguma";

    private Context mContext;
    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private View mMyView;

    private EditText mEditId;
    private EditText mEditPw;
    private TextView mBtnFind;
    private TextView mBtnLogin;
    private LoginButton mLoginKakao;
    private Button mBtnLoginNaver;
    private Button mBtnLoginKakao;
    private Button mBtnLoginFacebook;
    private Button mBtnRegister;

    private String mUserId;
    private String mUserPw;
    private String mUserName;
    private String mUserType;

    private SessionCallback mSessionCallback;

    private Handler mHandler;

    public EtcUserLoginView(Context context, View view, Handler handler)
    {
        mContext = context;
        mMyView = view;

        mService = GogumaService.getService();
        initRetrofit();

        mEditId = (EditText) view.findViewById(R.id.edit_id);
        mEditPw = (EditText) view.findViewById(R.id.edit_password);
        mBtnFind = (TextView) view.findViewById(R.id.btn_find);
        mBtnFind.setOnClickListener(mOnClickListener);
        mBtnLogin = (TextView) view.findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(mOnClickListener);
        mBtnLoginNaver = (Button) view.findViewById(R.id.btn_login_naver);
        mBtnLoginNaver.setOnClickListener(mOnClickListener);
//        mBtnLoginKakao = (Button) view.findViewById(R.id.btn_login_kakao);
//        mBtnLoginKakao.setOnClickListener(mOnClickListener);
        mLoginKakao = (LoginButton) view.findViewById(R.id.login_kakao);
        mBtnLoginFacebook = (Button) view.findViewById(R.id.btn_login_facebook);
        mBtnLoginFacebook.setOnClickListener(mOnClickListener);
        mBtnRegister = (Button) view.findViewById(R.id.btn_move_register);
        mBtnRegister.setOnClickListener(mOnClickListener);

        mHandler = handler;
    }

    public void setVisible(boolean visible)
    {
        mMyView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

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
                case R.id.btn_login:
                    login();
                    break;

                case R.id.login_kakao:
                    mSessionCallback = new SessionCallback();
                    Session.getCurrentSession().addCallback(mSessionCallback);

                    mLoginKakao.performClick();
                    break;

                case R.id.btn_move_register:
                    Intent intent = new Intent(mContext, EtcUserRegistActivity.class);
                    mContext.startActivity(intent);
                    break;

                default:
                    break;
            }
        }
    };

    private void login()
    {
        final String userId = mEditId.getText().toString();
        final String userPw = mEditPw.getText().toString();

        Log.i(LOG, "============> mRetrofitService : " + mRetrofitService);

        if(mRetrofitService != null && isValidInfo(userId, userPw))
        {
            mRetrofitService.getUser(userId).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response)
                {
                    if(response.body() == null)
                        Log.i(LOG, "=========> onResponse is null");

                    try {
                        String userColumn = response.body().string();

                        Log.i(LOG, "get : " + userColumn);

                        if(userColumn.isEmpty())
                        {
                            retryDialog("아이디가 없습니다.");
                            return;
                        }

                        parseUserColumn(userColumn);

                        if(mUserPw.equals(userPw))
                        {
                            InputMethodManager keyboard = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            keyboard.hideSoftInputFromWindow(mEditId.getWindowToken(), 0);
                            keyboard.hideSoftInputFromWindow(mEditPw.getWindowToken(), 0);

                            mEditId.setText("");
                            mEditPw.setText("");

                            if(mService != null)
                            {
                                mService.setConnectionState(ConnectionState.LOGON, userId, mUserName, "");
                                checkExistStore();

                                mHandler.sendEmptyMessage(EtcActivity.MSG_SUCCESS_LOGIN);
                                setVisible(false);
                            }
                        }
                        else
                            retryDialog("비밀번호가 일치하지 않습니다.");



                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Log.i(LOG, "=========> onFailure : " + t.toString());
                }
            });
        }
    }

    private void checkExistStore()
    {
        if(mRetrofitService != null)
        {
            mRetrofitService.showOwnStoreList(mService.getCurrentUser()).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    ResponseBody body = response.body();

                    try
                    {
                        if(body == null)
                        {
                            retryDialog("점포 목록 가져오기 실패");
                            return;
                        }

                        mService.setExistStore(!body.string().equals("[]"));
                    }
                    catch (IOException e) {}
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {

                }
            });
        }
    }

    private void register()
    {
        String userID = mEditId.getText().toString();
        String userPW = mEditPw.getText().toString();

        if(isValidInfo(userID, userPW))
        {
            HashMap<String, String> map = new HashMap<>();

            map.put("userId", userID);
            map.put("userPw", userPW);
            map.put("userName", userID);
            map.put("userType", Define.TYPE_PRODUCER);

            if(mRetrofitService != null)
            {
                mRetrofitService.createUser(map).enqueue(new Callback<ResponseBody>()
                {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response)
                    {
                        Log.i(LOG, "register : " + response.body());

                        if(response.body() == null)
                            retryDialog("이미 등록된 아이디 입니다.");
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage("회원가입 성공.")
                                    .setPositiveButton("확인", mOnConfirmListener)
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

    private boolean isValidInfo(String userID, String userPW)
    {
        if(userID.equals(""))
        {
            retryDialog("아이디를 입력해 주세요.");
            return false;
        }
        else if(userID.contains(" "))
        {
            retryDialog("아이디에 공백이 있습니다.");
            return false;
        }
        else if(userID.contains("!") || userID.contains("@") || userID.contains("#") || userID.contains("$") ||
                userID.contains("%") || userID.contains("^") || userID.contains("&") || userID.contains("*") ||
                userID.contains("(") || userID.contains(")") || userID.contains("_") || userID.contains("+") ||
                userID.contains("-") || userID.contains("=") || userID.contains("{") || userID.contains("}") ||
                userID.contains("[") || userID.contains("]") || userID.contains(":") || userID.contains("\"") ||
                userID.contains(";") || userID.contains("\'") || userID.contains("<") || userID.contains(">") ||
                userID.contains("?") || userID.contains(",") || userID.contains(".") || userID.contains("/"))
        {
            retryDialog("아이디에 특수문자가 있습니다.");
            return false;
        }
        else if(userPW.equals(""))
        {
            retryDialog("비밀번호를 입력해 주세요.");
            return false;
        }
        else if(userPW.contains(" "))
        {
            retryDialog("비밀번호에 공백이 있습니다.");
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
            mEditPw.setText("");
        }
    };


    private class SessionCallback implements ISessionCallback
    {

        @Override
        public void onSessionOpened()
        {
            Log.e(LOG, "=======================> onSessionOpened");
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception)
        {
            Log.e(LOG, "=======================> onSessionOpenFailed");

        }
    }

}
