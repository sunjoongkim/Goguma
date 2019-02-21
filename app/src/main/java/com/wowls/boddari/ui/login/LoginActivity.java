package com.wowls.boddari.ui.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.wowls.boddari.R;
import com.wowls.boddari.define.ConnectionState;
import com.wowls.boddari.define.Define;
import com.wowls.boddari.retrofit.RetrofitService;
import com.wowls.boddari.service.GogumaService;
import com.wowls.boddari.ui.etc.user.regist.EtcUserRegistActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends Activity
{
    public final static String LOG = "Goguma";

    private final static String KEY_NICKNAME = "properties.nickname";
    private final static String KEY_PROFILE_IMAGE = "properties.profile_image";
    private final static String KEY_EMAIL_ACCOUNT = "kakao_account.email";

    private GogumaService mService;
    private RetrofitService mRetrofitService;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etc_user_login_view);

        mService = GogumaService.getService();
        initRetrofit();

        mEditId = (EditText) findViewById(R.id.edit_id);
        mEditPw = (EditText) findViewById(R.id.edit_password);
        mBtnFind = (TextView) findViewById(R.id.btn_find);
        mBtnFind.setOnClickListener(mOnClickListener);
        mBtnLogin = (TextView) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(mOnClickListener);
        mBtnLoginNaver = (Button) findViewById(R.id.btn_login_naver);
        mBtnLoginNaver.setOnClickListener(mOnClickListener);
        mLoginKakao = (LoginButton) findViewById(R.id.login_kakao);
        mBtnLoginFacebook = (Button) findViewById(R.id.btn_login_facebook);
        mBtnLoginFacebook.setOnClickListener(mOnClickListener);
        mBtnRegister = (Button) findViewById(R.id.btn_move_register);
        mBtnRegister.setOnClickListener(mOnClickListener);

        mSessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(mSessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

                case R.id.btn_move_register:
                    Intent intent = new Intent(LoginActivity.this, EtcUserRegistActivity.class);
                    startActivity(intent);
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
                            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            keyboard.hideSoftInputFromWindow(mEditId.getWindowToken(), 0);
                            keyboard.hideSoftInputFromWindow(mEditPw.getWindowToken(), 0);

                            mEditId.setText("");
                            mEditPw.setText("");

                            if(mService != null)
                            {
                                mService.setConnectionState(ConnectionState.LOGON, userId, mUserName, "");
                                checkExistStore();
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

    private void register(final String userId, final String userNick, final String imagePath)
    {
        HashMap<String, String> map = new HashMap<>();

        map.put("userId", userId);
        map.put("userPw", "1");
        map.put("userName", userNick);
        map.put("userType", Define.TYPE_PRODUCER);

        if(mRetrofitService != null)
        {
            mRetrofitService.createUser(map).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response)
                {
                    Log.i(LOG, "register : " + response.body());

                    if(mService != null)
                    {
                        mService.setConnectionState(ConnectionState.LOGON, userId, userNick, imagePath);
                        checkExistStore();
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


    private void checkExistStore()
    {
        Log.i(LOG, "========> checkExistStore ");
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
                        finish();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception)
        {
            Log.e(LOG, "=======================> onSessionOpenFailed");

        }

        private void requestMe()
        {
            List<String> keys = new ArrayList<>();
            keys.add(KEY_NICKNAME);
            keys.add(KEY_PROFILE_IMAGE);
            keys.add(KEY_EMAIL_ACCOUNT);

            UserManagement.getInstance().me(keys, new MeV2ResponseCallback()
            {

                @Override
                public void onFailure(ErrorResult errorResult)
                {
                    String message = "failed to get user info. msg=" + errorResult;
                    Log.e(LOG, message);
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult)
                {

                }

                @Override
                public void onSuccess(MeV2Response result)
                {
                    register(Define.BY_KAKAO + String.valueOf(result.getId()), result.getNickname(), result.getProfileImagePath());
                }
            });

        }
    }

}
