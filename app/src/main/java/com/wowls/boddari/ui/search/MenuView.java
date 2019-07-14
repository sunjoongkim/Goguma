package com.wowls.boddari.ui.search;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wowls.boddari.R;

import java.io.File;
import java.util.HashMap;
import java.util.Stack;

public class MenuView {

    private RelativeLayout mDrawerMenu;
    private ImageView mBtnHome;
    private LinearLayout mBtnLink;
    private LinearLayout mBtnLearn;
    private LinearLayout mBtnModel;
    private LinearLayout mBtnRecognition;
    private LinearLayout mBtnGuide;

    private SearchActivity mSearchActivity;
    private HashMap<Menu, Stack<Fragment>> mTopStack;

    private File mSelectedLink;
    private boolean mIsCreate;
    private boolean mHasLinkFile;

    public MenuView(SearchActivity activity, View view, MenuItemClickListener listener) {
        mSearchActivity = activity;
        mMenuItemClickListener = listener;

        mDrawerMenu = (RelativeLayout) view.findViewById(R.id.view_menu);

//        mBtnLink = (LinearLayout) view.findViewById(R.id.btn_link);
//        mBtnLink.setOnClickListener(mOnClickListener);
//        mBtnLearn = (LinearLayout) view.findViewById(R.id.btn_learn);
//        mBtnLearn.setOnClickListener(mOnClickListener);
//        mBtnModel = (LinearLayout) view.findViewById(R.id.btn_model);
//        mBtnModel.setOnClickListener(mOnClickListener);
//        mBtnRecognition = (LinearLayout) view.findViewById(R.id.btn_recognition);
//        mBtnRecognition.setOnClickListener(mOnClickListener);
//        mBtnGuide = (LinearLayout) view.findViewById(R.id.btn_guide);
//        mBtnGuide.setOnClickListener(mOnClickListener);
    }

    public void setHasFile(boolean hasFile) {
        mHasLinkFile = hasFile;
    }

    public void updateLinkEnabled() {
        mBtnLink.setEnabled(mHasLinkFile);
    }

    public void updateLearnEnabled(boolean enable) {
        mBtnLearn.setEnabled(enable);
    }

    public void updateRecognitionEnabled(boolean enable) {
        mBtnRecognition.setEnabled(enable);
    }

    public View getDrawerMenu() {
        return mDrawerMenu;
    }

//    public void openDrawer() {
//        mSearchActivity.openDrawer(mDrawerMenu);
//    }
//
//    public void closeDrawer() {
//        mSearchActivity.closeDrawer(mDrawerMenu);
//    }

    public void sendSelectedFile(File file, boolean isCreate) {
        mSelectedLink = file;
        mIsCreate = isCreate;
    }

    public File getSelectedFile() {
        return mSelectedLink;
    }


    private MenuItemClickListener mMenuItemClickListener;

    public interface MenuItemClickListener {
        void onClickItem(Menu item);
    }

    public interface ListFileListener {
        void onFileCreated(File file);
        void onFileSelected(File file);
    }

    public interface BackPressedListener {
        void onBackPressed();
    }


    private final static int MSG_NONE = -1;
    private final static int MSG_MOVE_FRAGMENT = MSG_NONE + 1;

    private final static int DELAY_MOVE_FRAGMENT = 300;

    private MenuHandler mMenuHandler = new MenuHandler();

    private class MenuHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_MOVE_FRAGMENT:

                    break;

            }
        }
    }
}
