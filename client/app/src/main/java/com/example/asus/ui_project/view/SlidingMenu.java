package com.example.asus.ui_project.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

public class SlidingMenu extends HorizontalScrollView {
    /**
     * 屏幕宽度
     */
    private int mScreenWidth;
    /**
     * dp
     */
    private int mMenuRightPadding = 200;
    /**
     * 菜单的宽度
     */
    public int mMenuWidth;
    public boolean ON = false;
    private ViewGroup mMenu;
    private float x = 0;

    private boolean once;


    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth =outMetrics.widthPixels;
        mMenuRightPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 200, context
                        .getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
         * 显示的设置一个宽度
         */
        if (!once) {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) wrapper.getChildAt(0);
            ViewGroup content = (ViewGroup) wrapper.getChildAt(1);
            mMenuWidth = mScreenWidth - mMenuRightPadding;
            mMenu.getLayoutParams().width = mMenuWidth;
            content.getLayoutParams().width = mScreenWidth;
            once=true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            // 将菜单隐藏
            this.scrollTo(mMenuWidth, 0);
            once = true;
        }
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return false;
//    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int event = ev.getAction();
        switch (event) {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if (scrollX <= mMenuWidth - 100 && !ON) {
                    {
                        this.smoothScrollTo(0, 0);
                        ON = true;
                    }
                } else if (scrollX >= 100 && ON) {
                    this.smoothScrollTo(mMenuWidth, 0);
                    ON = false;
                } else if (x > mScreenWidth - 150 && ON) {
                    this.smoothScrollTo(mMenuWidth, 0);
                    ON = false;
                    x = 0;
                }//
                else if (ON)
                    this.smoothScrollTo(0, 0);
                else
                    this.smoothScrollTo(mMenuWidth, 0);
                return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);
        float scale = l * 1.0f / mMenuWidth;
        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale);//动画效果

    }


}



