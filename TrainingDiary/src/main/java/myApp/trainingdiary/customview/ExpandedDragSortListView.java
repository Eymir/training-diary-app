package myApp.trainingdiary.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.mobeta.android.dslv.DragSortListView;

public class ExpandedDragSortListView extends DragSortListView{
	 private android.view.ViewGroup.LayoutParams params;
	    private int old_count = 0;
	public ExpandedDragSortListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
    @Override
	protected void onDraw(Canvas canvas) {
        if (getCount() != old_count) {
            old_count = getCount();
            params = getLayoutParams();
            params.height = getCount() * (getCount() > 0 ? getChildAt(0).getHeight() : 0);
            setLayoutParams(params);
        }

        super.onDraw(canvas);
    }

}
