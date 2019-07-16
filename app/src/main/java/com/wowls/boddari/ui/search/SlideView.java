package com.wowls.boddari.ui.search;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wowls.boddari.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SlideView {

    private Context mContext;

    private View mMyView;
    private FrameLayout mSearchView;

    private ImageView mBtnManageStore;
    private LinearLayout mSearchBox;
    private ImageView mBtnDropdown;
    private EditText mSearchText;
    private ImageView mBtnSearch;
    private ImageView mBtnEvent;

    private ViewGroup.LayoutParams mBtnStoreParams;
    private ViewGroup.LayoutParams mBtnEventParams;

    private float mBtnStoreWidth;
    private float mBtnEventWidth;

    private TextView mTitle;
    private InputMethodManager mInputMethod;

    public SlideView(Context context, View view, SlideViewListener listener) {
        mContext = context;
        mSlideViewListener = listener;

        mMyView = view;
        mSearchView = (FrameLayout) view.findViewById(R.id.search_layout);

        mBtnManageStore = (ImageView) view.findViewById(R.id.btn_manage_store);
        mBtnManageStore.setOnClickListener(mOnClickListener);
        mSearchBox = (LinearLayout) view.findViewById(R.id.search_box);
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
        initSlideLayout();
    }

    private void initSlideLayout() {
        mBtnStoreParams = (ViewGroup.LayoutParams) mBtnManageStore.getLayoutParams();
        mBtnEventParams = (ViewGroup.LayoutParams) mBtnEvent.getLayoutParams();

        mBtnStoreWidth = mBtnStoreParams.width;
        mBtnEventWidth = mBtnEventParams.width;

        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mMyView.getLayoutParams();
        ViewGroup.LayoutParams searchParams = (ViewGroup.LayoutParams) mSearchView.getLayoutParams();

        params.height = (mContext.getResources().getDisplayMetrics().heightPixels / 2) + (searchParams.height / 2);

        mMyView.setLayoutParams(params);
    }

    public void updateSlideLayout(float alpha) {
        mSearchBox.setAlpha(1.0f - alpha);

        mBtnStoreParams.width = (int) (mBtnStoreWidth + (mBtnStoreWidth * alpha));
        mBtnEventParams.width = (int) (mBtnEventWidth + (mBtnEventWidth * alpha));

        mBtnManageStore.setLayoutParams(mBtnStoreParams);
        mBtnEvent.setLayoutParams(mBtnEventParams);
    }

    public String getTitle() {
        return mSearchText.getText().toString();
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
