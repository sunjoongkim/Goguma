package com.wowls.bottari.ui.search.pager;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.wowls.bottari.define.Define;


public class SearchPagerLayout extends FrameLayout
{
    private static final String LOGTAG = "Goguma";

    private float mScale = Define.BIG_SCALE;


    public SearchPagerLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SearchPagerLayout(Context context)
    {
        super(context);
    }

    public void setScaleBoth(float scale)
    {
        mScale = scale;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        canvas.scale(mScale, mScale, width/2, height/2);
    }
}
