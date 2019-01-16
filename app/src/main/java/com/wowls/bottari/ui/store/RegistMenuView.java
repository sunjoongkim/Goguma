package com.wowls.bottari.ui.store;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wowls.bottari.R;
import com.wowls.bottari.data.MenuInfo;
import com.wowls.bottari.define.Define;
import com.wowls.bottari.adapter.MenuListAdapter;
import com.wowls.bottari.retrofit.RetrofitService;
import com.wowls.bottari.service.GogumaService;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistMenuView
{
    public final static String LOG = "Goguma";

    private Context mContext;
    private View mMyView;
    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private RecyclerView mListView;
    private MenuListAdapter mListAdapter;

    private ArrayList<MenuInfo> mMenuList;

    private TextView mStoreId;
    private Button mBtnAddMenu;
    private Button mBtnEditMenu;

    private AlertDialog mEditDialog;
    private EditText mEditMenu;
    private EditText mEditPrice;
    private Button mBtnSubmit;

    private boolean mIsEditMode = false;

    public RegistMenuView(Context context, View view, RetrofitService service)
    {
        mContext = context;
        mMyView = view;
        mRetrofitService = service;

        mStoreId = (TextView) view.findViewById(R.id.text_store_id);
        mListView = (RecyclerView) view.findViewById(R.id.menu_list_view);
        mListView.setLayoutManager(new LinearLayoutManager(context));
        mBtnAddMenu = (Button) view.findViewById(R.id.btn_add);
        mBtnAddMenu.setOnClickListener(mOnClickListener);
        mBtnEditMenu = (Button) view.findViewById(R.id.btn_edit);
        mBtnEditMenu.setOnClickListener(mOnClickListener);

        mMenuList = new ArrayList<>();
    }

    public void setVisible(boolean visible)
    {
        mMyView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);

        if(visible)
        {
        }
    }

    public void setService(GogumaService service)
    {
        mService = service;
    }

    public void setMenuList(ArrayList<MenuInfo> list)
    {
        if(list == null || list.isEmpty())
        {
            if(mService != null)
                mStoreId.setText(mService.getCurrentUser());

            return;
        }

        mMenuList = list;

        initListAdapter();
    }

    public void registMenu(String name, String price)
    {
        HashMap<String, String> map = new HashMap<>();

        map.put(Define.KEY_MENU_STORE_ID, mService.getCurrentUser());
        map.put(Define.KEY_MENU_NAME, name);
        map.put(Define.KEY_MENU_PRICE, price);

        if(mRetrofitService != null && mService != null)
        {
            mRetrofitService.saveMenuInfo(mService.getCurrentUser(), mService.getCurrentUser(), map).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    Log.i(LOG, "register : " + response.body());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Log.i(LOG, "onFailure : " + t.toString());
                }
            });
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_add:
                    editDialog();
                    break;

                case R.id.btn_edit:
                    mIsEditMode = !mIsEditMode;
                    mListAdapter = new MenuListAdapter(mMenuList, mContext, mIsEditMode);
                    mListView.setAdapter(mListAdapter);
                    break;

                case R.id.btn_submit:
                    String menu = mEditMenu.getText().toString();
                    String price = mEditPrice.getText().toString();

                    registMenu(menu, price);

                    MenuInfo info = new MenuInfo(menu, price);
                    mMenuList.add(info);

                    if(mListAdapter == null)
                        initListAdapter();
                    else
                        mListAdapter.notifyItemInserted(mMenuList.size() - 1);

                    mEditDialog.dismiss();
                    break;
            }
        }
    };

    private void initListAdapter()
    {
        mListAdapter = new MenuListAdapter(mMenuList, mContext, mIsEditMode);
        mListView.setAdapter(mListAdapter);
    }

    public void editDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        View view = LayoutInflater.from(mContext).inflate(R.layout.store_manage_menu_edit_dialog, null, false);
        builder.setView(view);

        mEditMenu = (EditText) view.findViewById(R.id.edit_menu_name);
        mEditPrice = (EditText) view.findViewById(R.id.edit_menu_price);
        mBtnSubmit = (Button) view.findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(mOnClickListener);

        mEditDialog = builder.create();
        mEditDialog.show();
    }

}
