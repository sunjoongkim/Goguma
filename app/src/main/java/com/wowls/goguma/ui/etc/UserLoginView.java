package com.wowls.goguma.ui.etc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wowls.goguma.R;
import com.wowls.goguma.define.ConnectionState;
import com.wowls.goguma.define.Define;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.service.GogumaService;
import com.wowls.goguma.ui.store.StoreFragment;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserLoginView
{
    public final static String LOG = "Goguma";

    private Context mContext;
    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private View mMyView;

    private EditText mEditId;
    private EditText mEditPw;
    private Button mBtnLogin;
    private Button mBtnRegister;

    private String mUserId;
    private String mUserPw;
    private String mUserName;
    private String mUserType;

    private Handler mHandler;

    public UserLoginView(Context context, View view, Handler handler, RetrofitService service)
    {
        mContext = context;
        mMyView = view;
        mRetrofitService = service;

        mEditId = (EditText) view.findViewById(R.id.edit_id);
        mEditPw = (EditText) view.findViewById(R.id.edit_password);
        mBtnLogin = (Button) view.findViewById(R.id.btn_move);
        mBtnLogin.setOnClickListener(mOnClickListener);
        mBtnRegister = (Button) view.findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(mOnClickListener);

        mHandler = handler;
    }

    public void setVisible(boolean visible)
    {
        mMyView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setService(GogumaService service)
    {
        mService = service;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.btn_move:
                    login();
                    break;

                case R.id.btn_register:
                    register();
                    break;

                default:
                    break;
            }
        }
    };

    private void login()
    {
        final String userID = mEditId.getText().toString();
        final String userPW = mEditPw.getText().toString();

        Log.i(LOG, "============> mRetrofitService : " + mRetrofitService);

        if(mRetrofitService != null && isValidInfo(userID, userPW))
        {
            mRetrofitService.getUser(userID).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response)
                {
                    if(response.body() == null)
                        Log.i(LOG, "=========> onResponse is null");

                    Log.i(LOG, "=========> onResponse : " + response.body().toString());

                    try {
                        String userColumn = response.body().string();

                        Log.i(LOG, "get : " + userColumn);

                        if(userColumn.isEmpty())
                        {
                            retryDialog("아이디가 없습니다.");
                            return;
                        }

                        parseUserColumn(userColumn);

                        Log.i(LOG, "===============> mUserPw : " + mUserPw);
                        Log.i(LOG, "===============> userPW : " + userPW);

                        if(mUserPw.equals(userPW))
                        {
                            mHandler.sendEmptyMessage(StoreFragment.MSG_SUCCESS_LOGIN);

                            InputMethodManager keyboard = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            keyboard.hideSoftInputFromWindow(mEditId.getWindowToken(), 0);
                            keyboard.hideSoftInputFromWindow(mEditPw.getWindowToken(), 0);

                            mEditId.setText("");
                            mEditPw.setText("");

                            if(mService != null)
                            {
                                mService.setConnectionState(ConnectionState.LOGON, userID);
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

}