package com.polites.android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Lenovo on 23.04.2014.
 */
public class FixedRatioFrameLayout extends FrameLayout {
    public FixedRatioFrameLayout(Context context) {
        super(context);
    }

    public FixedRatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedRatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
