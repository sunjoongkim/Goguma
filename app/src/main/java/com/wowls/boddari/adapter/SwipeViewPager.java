package com.wowls.boddari.adapter;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SwipeViewPager extends ViewPager
{
    public SwipeViewPager(Context context)
    {
        super(context);
    }

    public SwipeViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if(MotionEventCompat.getActionMasked(ev) == MotionEvent.ACTION_MOVE)
        {
            // ignore move action
        }
        else
        {
            if(super.onInterceptTouchEvent(ev))
                super.onTouchEvent(ev);
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        return MotionEventCompat.getActionMasked(ev) != MotionEvent.ACTION_MOVE && super.onTouchEvent(ev);
    }
}
