package com.example.andrea.lyrics.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by andrea on 26/03/17.
 */

public class InterceptableScrollView extends ScrollView {

    private boolean scrollable = true;

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public InterceptableScrollView(Context context) {
        super(context);
    }

    public InterceptableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollable) return super.onTouchEvent(ev);
        return false;
    }
}
