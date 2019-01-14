package com.wowls.bottari.ui.store.open;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wowls.bottari.R;
import com.wowls.bottari.adapter.MenuListAdapter;
import com.wowls.bottari.data.MenuInfo;
import com.wowls.bottari.retrofit.RetrofitService;
import com.wowls.bottari.service.GogumaService;

import java.util.ArrayList;

public class OpenFragmentThree extends Fragment
{
    private static final String LOG = "Goguma";

    private static OpenFragmentThree mMyFragment;
    private Context mContext;

    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private RecyclerView mListView;
    private MenuListAdapter mListAdapter;

    private ArrayList<MenuInfo> mMenuList = new ArrayList<>();

    private Button mBtnAddMenu;

    private AlertDialog mEditDialog;
    private EditText mEditMenu;
    private EditText mEditPrice;
    private Button mBtnSubmit;

    private boolean mIsEditMode = false;

    public static OpenFragmentThree getInstance()
    {
        Bundle args = new Bundle();

        OpenFragmentThree fragment = new OpenFragmentThree();
        fragment.setArguments(args);

        return fragment;
    }

    public static OpenFragmentThree getFragment()
    {
        return mMyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.store_open_3, container, false);

        mMyFragment = this;

        mContext = getContext();
        mService = GogumaService.getService();

        mListView = (RecyclerView) view.findViewById(R.id.menu_list_view);
        mListView.setLayoutManager(new LinearLayoutManager(mContext));
        mBtnAddMenu = (Button) view.findViewById(R.id.btn_add);
        mBtnAddMenu.setOnClickListener(mOnClickListener);

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

    public boolean isEmptyMenuList()
    {
        if(mMenuList == null)
            return true;

        return mMenuList.size() == 0;
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

                case R.id.btn_submit:
                    String menu = mEditMenu.getText().toString();
                    String price = mEditPrice.getText().toString();

                    if(menu.isEmpty())
                    {
                        retryDialog("메뉴를 입력해주세요.");
                        break;
                    }
                    else if(price.isEmpty())
                    {
                        retryDialog("가격을 입력해주세요.");
                        break;
                    }

                    MenuInfo info = new MenuInfo(menu, price);
                    mMenuList.add(info);

                    if(mService != null)
                        mService.setOpenMenuList(mMenuList);

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
        mListAdapter = new MenuListAdapter(mMenuList, mContext, false);
        mListView.setAdapter(mListAdapter);
    }

    private void editDialog()
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

    private void retryDialog(String comment)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(comment)
                .setNegativeButton("확인", null)
                .create()
                .show();
    }

}
