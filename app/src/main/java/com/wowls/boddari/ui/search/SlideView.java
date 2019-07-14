package com.wowls.boddari.ui.search;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wowls.boddari.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SlideView {

    private Context mContext;

    private ImageView mBtnManageStore;
    private ImageView mBtnDropdown;
    private EditText mSearchText;
    private ImageView mBtnSearch;
    private ImageView mBtnEvent;

    private TextView mTitle;
    private InputMethodManager mInputMethod;

    public SlideView(Context context, View view, SlideViewListener listener) {
        mContext = context;
        mSlideViewListener = listener;

        mBtnManageStore = (ImageView) view.findViewById(R.id.btn_manage_store);
        mBtnManageStore.setOnClickListener(mOnClickListener);
        mBtnDropdown = (ImageView) view.findViewById(R.id.btn_dropdown);
        mBtnDropdown.setOnClickListener(mOnClickListener);
        mSearchText = (EditText) view.findViewById(R.id.edit_keyword);
        mSearchText.setOnClickListener(mOnClickListener);
        mBtnSearch = (ImageView) view.findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(mOnClickListener);
        mBtnEvent = (ImageView) view.findViewById(R.id.btn_event);
        mBtnEvent.setOnClickListener(mOnClickListener);
//        mTitle = (TextView) view.findViewById(R.id.link_title);

        mInputMethod = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);


    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    private SlideViewListener mSlideViewListener;

    public interface SlideViewListener {
        void onCloseSlide();
    }



}
