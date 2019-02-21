package com.wowls.boddari.ui.store.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wowls.boddari.R;
import com.wowls.boddari.data.MenuInfo;

import java.util.ArrayList;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MenuViewHolder>
{
    private static final String LOG = "Goguma";

    private ArrayList<MenuInfo> mMenuList;
    private Context mContext;
    private boolean mIsEditMode = false;

    public MenuListAdapter(ArrayList<MenuInfo> list, Context context, boolean isEditMode)
    {
        mMenuList = list;
        mContext = context;
        mIsEditMode = isEditMode;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        Log.i(LOG, "==============> onCreateViewHolder : " + i);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.store_manage_menu_item_view, viewGroup, false);
        MenuViewHolder holder = new MenuViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder viewHolder, final int position)
    {
        viewHolder.menuName.setText(mMenuList.get(position).getMenuName());
        viewHolder.menuPrice.setText(mMenuList.get(position).getMenuPrice());
        viewHolder.delete.setVisibility(mIsEditMode ? View.VISIBLE : View.INVISIBLE);

        Log.i(LOG, "==============> onBindViewHolder menu : " + mMenuList.get(position).getMenuName());
        Log.i(LOG, "==============> onBindViewHolder price : " + mMenuList.get(position).getMenuPrice());
    }

    @Override
    public int getItemCount()
    {
        return mMenuList.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView menuName;
        protected TextView menuPrice;
        protected Button delete;

        public MenuViewHolder(@NonNull View view)
        {
            super(view);

            menuName = (TextView) view.findViewById(R.id.text_menu_name);
            menuPrice = (TextView) view.findViewById(R.id.text_menu_price);
            delete = (Button) view.findViewById(R.id.btn_delete);
            delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mMenuList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), mMenuList.size());
                }
            });
        }
    }
}
