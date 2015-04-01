package com.ecloudiot.framework.view;

import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.widget.MultiViewpagerWidget;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class ViewMultiViewpagerContainer extends LinearLayout implements OnPageChangeListener {
	private ViewPager mPager;
    boolean mNeedsRedraw = false;
    private MultiViewpagerWidget multiViewpagerWidget;
    private String TAG = "ViewMultiViewpagerContainer";

	public ViewMultiViewpagerContainer(Context context) {
		super(context);
	}

    public ViewMultiViewpagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
 
    private void init() {
        //Disable clipping of children so non-selected pages are visible
        setClipChildren(false);
 
        //Child clipping doesn't work with hardware acceleration in Android 3.x/4.x
        //You need to set this value here if using hardware acceleration in an
        // application targeted at these releases.
    }
    
    public MultiViewpagerWidget getMultiViewpagerWidget() {
		return multiViewpagerWidget;
	}

	public void setMultiViewpagerWidget(MultiViewpagerWidget multiViewpagerWidget) {
		this.multiViewpagerWidget = multiViewpagerWidget;
	}

	@Override
    protected void onFinishInflate() {
        try {
            mPager = (ViewPager) getChildAt(0);
            mPager.setOnPageChangeListener(this);
        } catch (Exception e) {
            throw new IllegalStateException("The root child of PagerContainer must be a ViewPager");
        }
    }
 
    public ViewPager getViewPager() {
        return mPager;
    }
	
	@Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Force the container to redraw on scrolling.
        //Without this the outer pages render initially and then stay static
		LogUtil.d(TAG, "onPageScrolled");
        if (mNeedsRedraw) invalidate();
    }
 
    @Override
    public void onPageSelected(int position) { 
    	LogUtil.d(TAG, this.multiViewpagerWidget + "");
//    	String fTag = ((FragmentPagerAdapter)this.multiViewpagerWidget.getmPager().getAdapter()).getItem(position).getTag();
//    	int fid = ((FragmentPagerAdapter)this.multiViewpagerWidget.getmPager().getAdapter()).getItem(position).getId();
//    	LogUtil.d(TAG, "tag = "+fTag);
//    	Toast.makeText(IntentUtil.getActivity(),"id = "+fid + ", tag = "+fTag, Toast.LENGTH_SHORT).show();
    	if (this.multiViewpagerWidget!=null && this.multiViewpagerWidget.getOnPageSelectedListener() != null) {
    		this.multiViewpagerWidget.getOnPageSelectedListener().onPageSelected(position);
		}
    }
 
    @Override
    public void onPageScrollStateChanged(int state) {
    	LogUtil.d(TAG, "onPageScrollStateChanged");
        mNeedsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
    }
    
    private Point mCenter = new Point();
    private Point mInitialTouch = new Point();
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //We capture any touches not already handled by the ViewPager
        // to implement scrolling from a touch outside the pager bounds.
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialTouch.x = (int)ev.getX();
                mInitialTouch.y = (int)ev.getY();
            default:
                ev.offsetLocation(mCenter.x - mInitialTouch.x, mCenter.y - mInitialTouch.y);
                break;
        }
 
        return mPager.dispatchTouchEvent(ev);
    }

}
