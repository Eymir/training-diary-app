package com.polites.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import ru.adhocapp.instaprint.R;

/**
* A frame container which maintains a fixed aspect ratio.
*
* Usage examples for 4:3 aspect ratio landscape:
*
*
* Add a new dimension value:
* <resources>
* <item name="top_image_aspect_ratio" format="float" type="dimen">1.3333</item>
*
*
* Add a new xmlns to the root element of the layout:
* <Layout
* xmlns:android="http://schemas.android.com/apk/res/android"
* xmlns:YOUR_APP="http://schemas.android.com/apk/res-auto"
*
*
* <LinearLayout
* android:layout_width="match_parent"
* android:layout_height="wrap_content"
* android:orientation="horizontal"
* android:weightSum="3">
* <!-- The HEIGHT will be dynamic,
* because the width will have a value greater than 0,
* because of the layout_weight. -->
* <com.triposo.barone.FixedAspectRatioFrameLayout
* android:layout_height="0px"
* android:layout_width="0px"
* android:layout_weight="2"
* YOUR_APP:aspectRatio="@dimen/top_image_aspect_ratio">
* </LinearLayout>
*
*
* <RelativeLayout
* android:layout_width="match_parent"
* android:layout_height="match_parent">
* <!-- The HEIGHT will be dynamic, because layout_height is 0px. -->
* <com.triposo.barone.FixedAspectRatioFrameLayout
* android:layout_width="match_parent"
* android:layout_height="0px"
* YOUR_APP:aspectRatio="@dimen/top_image_aspect_ratio">
* </RelativeLayout>
*
*/
public class FixedAspectRatioLayout extends RelativeLayout {
    /**
     * (width / height)
     */
    private float aspectRatio;

    public FixedAspectRatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedAspectRatioLayout);
        aspectRatio = a.getFloat(R.styleable.FixedAspectRatioLayout_aspectRatio, 1.40952380952381f) - 0.0304595929658166f;
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);

        int finalHeight = (int) (originalWidth / aspectRatio);

        super.onMeasure(MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }
}