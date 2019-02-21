package com.wowls.boddari.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import net.daum.mf.map.api.MapView;

public class RemoveScrollMapView extends MapView
{
    public RemoveScrollMapView(Context context)
    {
        super(context);
    }

    public RemoveScrollMapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RemoveScrollMapView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        int action = ev.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN: // MapView에서 터치를 발생할 때, 부모뷰(ScrollView)가 TouchEvent를 가로채는 것을 막음
                getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP: // MapView에서 터치를 뗄때, 부모뷰(ScrollView)가 TouchEvent를 가로채는 것을 허용함
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        super.onTouchEvent(ev);
        return true;
    }
}