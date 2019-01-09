package com.wowls.goguma.ui.store.open;

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

import com.wowls.goguma.R;
import com.wowls.goguma.adapter.MenuListAdapter;
import com.wowls.goguma.data.MenuInfo;
import com.wowls.goguma.retrofit.RetrofitService;
import com.wowls.goguma.service.GogumaService;

import java.util.ArrayList;

public class OpenFragmentThree extends Fragment
{
    private static final String LOG = "Goguma";

    private Context mContext;

    private GogumaService mService;
    private RetrofitService mRetrofitService;

    private RecyclerView mListView;
    private MenuListAdapter mListAdapter;

    private ArrayList<MenuInfo> mMenuList = new ArrayList<>();

    private Button mBtnAddMenu;
    private Button mBtnEditMenu;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.store_open_3, container, false);

        mContext = getContext();
        mService = GogumaService.getService();

        mListView = (RecyclerView) view.findViewById(R.id.menu_list_view);
        mListView.setLayoutManager(new LinearLayoutManager(mContext));
        mBtnAddMenu = (Button) view.findViewById(R.id.btn_add);
        mBtnAddMenu.setOnClickListener(mOnClickListener);
        mBtnEditMenu = (Button) view.findViewById(R.id.btn_edit);
        mBtnEditMenu.setOnClickListener(mOnClickListener);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

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

                    MenuInfo info = new MenuInfo(menu, price);
                    mMenuList.add(info);

                    if(mService != null)
                        mService.setMenuList(mMenuList);

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
